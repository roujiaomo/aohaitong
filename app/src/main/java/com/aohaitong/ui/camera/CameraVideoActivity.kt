package com.aohaitong.ui.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.work.await
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.databinding.ActivityCameraVideoBinding
import com.aohaitong.kt.common.autoCleared
import com.example.android.camerax.video.extensions.getAspectRatio
import com.example.android.camerax.video.extensions.getAspectRatioString
import com.example.android.camerax.video.extensions.getNameString
import com.zhaoss.weixinrecorded.activity.EditVideoActivity
import com.zhaoss.weixinrecorded.activity.RecordedActivity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CameraVideoActivity : BaseActivity() {

    override fun getLayout() = R.layout.activity_camera_video

    override fun initView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        captureViewBinding = DataBindingUtil.setContentView(this, layout)
    }

    override fun initData() {
        initializeUI()
        lifecycleScope.launch {
            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }
            bindCaptureUsecase()
        }
    }

    override fun initEvent() {
    }

    // UI with ViewBinding
    private var captureViewBinding: ActivityCameraVideoBinding by autoCleared()
    private val captureLiveStatus = MutableLiveData<String>()

    private val cameraCapabilities = mutableListOf<CameraCapability>()

    private lateinit var videoCapture: VideoCapture<Recorder>
    private var currentRecording: Recording? = null
    private lateinit var recordingState: VideoRecordEvent

    // Camera UI  states and inputs
    enum class UiState {
        IDLE,       // Not recording, all UI controls are active.
        RECORDING,  // Camera is recording, only display Pause/Resume & Stop button.
        FINALIZED,  // Recording just completes, disable all RECORDING UI controls.
        RECOVERY    // For future use.
    }

    private var cameraIndex = 0
    private var qualityIndex = DEFAULT_QUALITY_IDX
    private var audioEnabled = false

    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(this) }
    private var enumerationDeferred: Deferred<Unit>? = null

    private suspend fun bindCaptureUsecase() {
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()

        val cameraSelector = getCameraSelector(cameraIndex)

        // create the user required QualitySelector (video resolution): we know this is
        // supported, a valid qualitySelector will be created.
        val quality = cameraCapabilities[cameraIndex].qualities[qualityIndex]
        val qualitySelector = QualitySelector.from(quality)

        captureViewBinding.previewView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val orientation = this@CameraVideoActivity.resources.configuration.orientation
            dimensionRatio = quality.getAspectRatioString(
                quality,
                (orientation == Configuration.ORIENTATION_PORTRAIT)
            )
        }
        val preview = Preview.Builder()
            .setTargetAspectRatio(quality.getAspectRatio(quality))
            .build().apply {
                setSurfaceProvider(captureViewBinding.previewView.surfaceProvider)
            }

        // build a recorder, which can:
        //   - record video/audio to MediaStore(only shown here), File, ParcelFileDescriptor
        //   - be used create recording(s) (the recording performs recording)
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                videoCapture,
                preview
            )
        } catch (exc: Exception) {
            // we are on main thread, let's reset the controls on the UI.
            Log.e(TAG, "Use case binding failed", exc)
            resetUIandState("bindToLifecycle failed: $exc")
        }
        enableUI(true)
    }

    /**
     * Kick start the video recording
     *   - config Recorder to capture to MediaStoreOutput
     *   - register RecordEvent Listener
     *   - apply audio request from user
     *   - start recording!
     * After this function, user could start/pause/resume/stop recording and application listens
     * to VideoRecordEvent for the current recording status.
     */
    @SuppressLint("MissingPermission")
    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val name = "CameraX-recording-" +
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
//            put(MediaStore.Video.Media.DURATION, 5)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            this.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setFileSizeLimit(5 * 1024 * 1024L)
            .setContentValues(contentValues)
            .build()

        // configure Recorder and Start recording to the mediaStoreOutput.
        currentRecording = videoCapture.output
            .prepareRecording(this, mediaStoreOutput)
            .apply { withAudioEnabled() }
            .start(mainThreadExecutor, captureListener)

        Log.i(TAG, "Recording started")
    }

    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status)
            recordingState = event

        updateUI(event)

        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
            lifecycleScope.launch {
                //跳转详情
//                val resultIntent = Intent()
//             val filePath = getFilePathFromContentUri(event.outputResults.outputUri,  context.contentResolver)
//
//                resultIntent.putExtra(
//                    RecordedActivity.INTENT_PATH, filePath
//                )
//                setResult(-1, resultIntent)
//                finish()
                val filePath = getFilePathFromContentUri(
                    event.outputResults.outputUri,
                    context.contentResolver
                )
                val intent = Intent(
                    context,
                    EditVideoActivity::class.java
                )
                intent.putExtra(RecordedActivity.INTENT_PATH, filePath)
                startActivityForResult(intent, RecordedActivity.REQUEST_CODE_KEY)
            }
        }
    }

    fun getFilePathFromContentUri(
        selectedVideoUri: Uri,
        contentResolver: ContentResolver
    ): String {
        var filePath: String = ""
        val filePathColumn = arrayOf<String>(MediaStore.MediaColumns.DATA)
        val cursor: Cursor? =
            contentResolver.query(selectedVideoUri, filePathColumn, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex: Int = it.getColumnIndex(filePathColumn[0])
            filePath = it.getString(columnIndex)
            it.close()
        }
        return filePath
    }

    /**
     * Retrieve the asked camera's type(lens facing type). In this sample, only 2 types:
     *   idx is even number:  CameraSelector.LENS_FACING_BACK
     *          odd number:   CameraSelector.LENS_FACING_FRONT
     */
    private fun getCameraSelector(idx: Int): CameraSelector {
        if (cameraCapabilities.size == 0) {
            Log.i(TAG, "Error: This device does not have any camera, bailing out")
            this.finish()
        }
        return (cameraCapabilities[idx % cameraCapabilities.size].camSelector)
    }

    data class CameraCapability(val camSelector: CameraSelector, val qualities: List<Quality>)

    /**
     * Query and cache this platform's camera capabilities, run only once.
     */
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider = ProcessCameraProvider.getInstance(this@CameraVideoActivity).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera =
                                provider.bindToLifecycle(this@CameraVideoActivity, camSelector)
                            QualitySelector
                                .getSupportedQualities(camera.cameraInfo)
                                .filter { quality ->
                                    listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD)
                                        .contains(quality)
                                }.also {
                                    cameraCapabilities.add(CameraCapability(camSelector, it))
                                }
                        }
                    } catch (exc: java.lang.Exception) {
                        Log.e(TAG, "Camera Face $camSelector is not supported")
                    }
                }
                qualityIndex = if (cameraCapabilities.isEmpty()) DEFAULT_QUALITY_IDX
                else cameraCapabilities[cameraIndex].qualities.size - 1
            }
        }
    }


    /**
     * Initialize UI. Preview and Capture actions are configured in this function.
     * Note that preview and capture are both initialized either by UI or CameraX callbacks
     * (except the very 1st time upon entering to this fragment in onCreateView()
     */
    @SuppressLint("ClickableViewAccessibility", "MissingPermission")
    private fun initializeUI() {
        captureViewBinding.cameraButton.apply {
            setOnClickListener {
                cameraIndex = (cameraIndex + 1) % cameraCapabilities.size
                // camera device change is in effect instantly:
                //   - reset quality selection
                //   - restart preview
                qualityIndex = cameraCapabilities.size - 1
                enableUI(false)
                lifecycleScope.launch {
                    bindCaptureUsecase()
                }
            }
            isEnabled = false
        }

        // React to user touching the capture button
        captureViewBinding.captureButton.apply {
            setOnClickListener {
                if (!this@CameraVideoActivity::recordingState.isInitialized ||
                    recordingState is VideoRecordEvent.Finalize
                ) {
                    enableUI(false)  // Our eventListener will turn on the Recording UI.
                    startRecording()
                } else {
                    when (recordingState) {
                        is VideoRecordEvent.Start -> {
                            currentRecording?.pause()
                            captureViewBinding.stopButton.visibility = View.VISIBLE
                        }
                        is VideoRecordEvent.Pause -> currentRecording?.resume()
                        is VideoRecordEvent.Resume -> currentRecording?.pause()
                        else -> throw IllegalStateException("recordingState in unknown state")
                    }
                }
            }
            isEnabled = false
        }

        captureViewBinding.stopButton.apply {
            setOnClickListener {
                // stopping: hide it after getting a click before we go to viewing fragment
                captureViewBinding.stopButton.visibility = View.INVISIBLE
                if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
                    return@setOnClickListener
                }

                val recording = currentRecording
                if (recording != null) {
                    recording.stop()
                    currentRecording = null
                }
                captureViewBinding.captureButton.setImageResource(R.drawable.ic_start)
            }
            // ensure the stop button is initialized disabled & invisible
            visibility = View.INVISIBLE
            isEnabled = false
        }

//        captureLiveStatus.value = getString(R.string.Idle)
    }

    /**
     * UpdateUI according to CameraX VideoRecordEvent type:
     *   - user starts capture.
     *   - this app disables all UI selections.
     *   - this app enables capture run-time UI (pause/resume/stop).
     *   - user controls recording with run-time UI, eventually tap "stop" to end.
     *   - this app informs CameraX recording to stop with recording.stop() (or recording.close()).
     *   - CameraX notify this app that the recording is indeed stopped, with the Finalize event.
     *   - this app starts VideoViewer fragment to view the captured result.
     */
    private fun updateUI(event: VideoRecordEvent) {
        val state = if (event is VideoRecordEvent.Status) recordingState.getNameString()
        else event.getNameString()
        when (event) {
            is VideoRecordEvent.Status -> {
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
            }
            is VideoRecordEvent.Start -> {
                showUI(CameraVideoActivity.UiState.RECORDING, event.getNameString())
            }
            is VideoRecordEvent.Finalize -> {
                showUI(CameraVideoActivity.UiState.FINALIZED, event.getNameString())
            }
            is VideoRecordEvent.Pause -> {
                captureViewBinding.captureButton.setImageResource(R.drawable.ic_resume)
            }
            is VideoRecordEvent.Resume -> {
                captureViewBinding.captureButton.setImageResource(R.drawable.ic_pause)
            }
        }

        val stats = event.recordingStats
        val size = stats.numBytesRecorded / 1000
        val time = java.util.concurrent.TimeUnit.NANOSECONDS.toSeconds(stats.recordedDurationNanos)
        var text = "${state}: recorded ${size}KB, in ${time}second"
        if (event is VideoRecordEvent.Finalize)
            text = "${text}\nFile saved to: ${event.outputResults.outputUri}"

        captureLiveStatus.value = text
        Log.i(TAG, "recording event: $text")
    }

    /**
     * Enable/disable UI:
     *    User could select the capture parameters when recording is not in session
     *    Once recording is started, need to disable able UI to avoid conflict.
     */
    private fun enableUI(enable: Boolean) {
        arrayOf(
            captureViewBinding.cameraButton,
            captureViewBinding.captureButton,
            captureViewBinding.stopButton,
            captureViewBinding.qualitySelection
        ).forEach {
            it.isEnabled = enable
        }
        // disable the camera button if no device to switch
        if (cameraCapabilities.size <= 1) {
            captureViewBinding.cameraButton.isEnabled = false
        }
        // disable the resolution list if no resolution to switch
        if (cameraCapabilities[cameraIndex].qualities.size <= 1) {
            captureViewBinding.qualitySelection.apply { isEnabled = false }
        }
    }

    /**
     * initialize UI for recording:
     *  - at recording: hide audio, qualitySelection,change camera UI; enable stop button
     *  - otherwise: show all except the stop button
     */
    private fun showUI(state: CameraVideoActivity.UiState, status: String = "idle") {
        captureViewBinding.let {
            when (state) {
                CameraVideoActivity.UiState.IDLE -> {
                    it.captureButton.setImageResource(R.drawable.ic_start)
                    it.stopButton.visibility = View.INVISIBLE

                    it.cameraButton.visibility = View.VISIBLE
                    it.qualitySelection.visibility = View.VISIBLE
                }
                CameraVideoActivity.UiState.RECORDING -> {
                    it.cameraButton.visibility = View.INVISIBLE
                    it.qualitySelection.visibility = View.INVISIBLE

                    it.captureButton.setImageResource(R.drawable.ic_pause)
                    it.captureButton.isEnabled = true
                    it.stopButton.visibility = View.VISIBLE
                    it.stopButton.isEnabled = true
                }
                CameraVideoActivity.UiState.FINALIZED -> {
                    it.captureButton.setImageResource(R.drawable.ic_start)
                    it.stopButton.visibility = View.INVISIBLE
                }
                else -> {
                    val errorMsg = "Error: showUI($state) is not supported"
                    Log.e(TAG, errorMsg)
                    return
                }
            }
//            it.captureStatus.text = status
        }
    }

    /**
     * ResetUI (restart):
     *    in case binding failed, let's give it another change for re-try. In future cases
     *    we might fail and user get notified on the status
     */
    private fun resetUIandState(reason: String) {
        enableUI(true)
        showUI(UiState.IDLE, reason)

        cameraIndex = 0
//        qualityIndex = DEFAULT_QUALITY_IDX
        audioEnabled = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == RecordedActivity.REQUEST_CODE_KEY) {
                val intent = Intent()
                intent.putExtra(
                    RecordedActivity.INTENT_PATH,
                    data.getStringExtra(RecordedActivity.INTENT_PATH)
                )
                intent.putExtra(
                    RecordedActivity.INTENT_DATA_TYPE,
                    RecordedActivity.RESULT_TYPE_VIDEO
                )
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    companion object {
        // default Quality selection if no input from UI
        const val DEFAULT_QUALITY_IDX = 0
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}