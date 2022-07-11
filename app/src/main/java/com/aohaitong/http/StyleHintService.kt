package com.aohaitong.http

/**
 * REST API access points
 */
interface StyleHintService {
    companion object {
        private const val LOCAL_SUFFIX = "jp"
    }

//    companion object {
//
//        private const val OS_TYPE = 1
//        private const val LOCAL_SUFFIX = "jp"
//    }
//
//    /**
//     * Get Report Reasons
//     */
//    @GET("v1/$LOCAL_SUFFIX/selection")
//    fun getReportSelection(@Query("code_type") codeType: Int): Single<Report>
//
//    /**
//     * This method used to post reason when delete outfit
//     */
//    @FormUrlEncoded
//    @POST("v1/$LOCAL_SUFFIX/outfit/detail/report")
//    fun reportOutfitDetail(
//        @Field("id_outfit") idOutfit: Int,
//        @Field("reason") reason: Int
//    ): Completable
//
//    /**
//     * This method used to post reason when delete comment
//     */
//    @FormUrlEncoded
//    @POST("v1/$LOCAL_SUFFIX/outfit/{id_outfit}/comment/report")
//    fun reportOutfitComment(
//        @Path("id_outfit") idOutfit: Int,
//        @Field("id_outfit_comment") idComment: Int,
//        @Field("reason") reason: Int
//    ): Completable
//
//    @GET("v2/$LOCAL_SUFFIX/outfit-comments")
//    fun getComments(
//        @Query("id_outfit") idOutfit: Int, @Query("result_limit") resultLimit: Int,
//        @Query("since_id") sinceId: Int, @Query("reply_to") replyTo: Int
//    ): Single<CommentResponse>
//
//    @POST("v2/$LOCAL_SUFFIX/outfit-comments")
//    fun postComment(@Body comment: PostCommentBody): Single<Comment>
//
//    @PATCH("v1/$LOCAL_SUFFIX/outfit/{id_outfit}/comment/{id_outfit_comment}/delete")
//    fun deleteComment(@Path("id_outfit") idOutfit: Int, @Path("id_outfit_comment") idOutfitComment: Int): Completable
//
//    @PATCH("v1/$LOCAL_SUFFIX/outfit/{id_outfit}/comment/{id_outfit_comment}/undelete")
//    fun unDeleteComment(
//        @Path("id_outfit") idOutfit: Int,
//        @Path("id_outfit_comment") idOutfitComment: Int
//    ): Completable
//
//    @GET("v1/$LOCAL_SUFFIX/user/mention")
//    fun getMentions(@Query("q") q: String, @Query("result_limit") resultLimit: Int): Single<MentionResponse>
//
//    @GET("v1/$LOCAL_SUFFIX/user/username/{username}/id")
//    fun getUserIdFromUsername(@Path("username") username: String): Single<UserIdResponse>
//
//    /**
//     * This method used to post user report outfit
//     */
//    @FormUrlEncoded
//    @POST("v1/$LOCAL_SUFFIX/user/{id_user_app}/profile/report")
//    fun reportUser(
//        @Path("id_user_app") idUserApp: Int,
//        @Field("reason") reason: Int
//    ): Completable
//
//    /**
//     * This method used to get push notification setting
//     */
//    @GET("v1/$LOCAL_SUFFIX/notification/setting")
//    fun getPushNotificationSetting(): Single<PushNotificationSettingResponse>
//
//    /**
//     * This method used to push notification setting
//     */
//    @FormUrlEncoded
//    @PUT("v1/$LOCAL_SUFFIX/notification/setting")
//    fun putNotificationSetting(
//        @Field("follow") follow: Boolean?,
//        @Field("star") star: Boolean?,
//        @Field("comment") comment: Boolean?,
//        @Field("mention") mention: Boolean?,
//        @Field("news") news: Boolean?,
//        @Field("saved_items") savedItems: Boolean?,
//        @Field("following_users_posts") followingUsersPosts: Boolean?
//    ): Completable
//
//    /**
//     * This method used to get block users
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/block")
//    fun getBlockUsers(): Single<BlockUserResponse>
//
//    /**
//     * This method used to block user
//     */
//    @POST("v1/$LOCAL_SUFFIX/user/{id_blocked_user}/block")
//    fun blockUser(@Path("id_blocked_user") idUserApp: Int): Completable
//
//    /**
//     * This method used to unblock user
//     */
//    @PATCH("v1/$LOCAL_SUFFIX/user/{id_blocked_user}/block")
//    fun unBlockUser(@Path("id_blocked_user") idUserApp: Int): Completable
//
//    /**
//     * This method used to get announcement information
//     */
//    @GET("v1/$LOCAL_SUFFIX/announcement/info")
//    fun getAnnouncementInformation(): Single<AnnouncementResponse>
//
//    /**
//     * This method used to get announcement information
//     */
//    @GET("v1/$LOCAL_SUFFIX/direct-message/info")
//    fun getDirectMessageInfo(): Single<DirectMesageResponse>
//
//    /**
//     * This method used to put region.
//     */
//    @FormUrlEncoded
//    @PUT("v1/$LOCAL_SUFFIX/user/region")
//    fun putRegion(@Field("region") region: String): Completable
//
//    /**
//     * This method used to put language.
//     */
//    @FormUrlEncoded
//    @PUT("v1/$LOCAL_SUFFIX/user/language")
//    fun putLanguage(@Field("language") language: String): Completable
//
//    /**
//     * This method used to logout sns.
//     */
//    @FormUrlEncoded
//    @POST("v1/$LOCAL_SUFFIX/user/logout")
//    fun logOutSNSUser(@Field("uuid") uuid: String): Completable
//
//    /**
//     * This method used update user profile
//     */
//    @Multipart
//    @PUT("v1/$LOCAL_SUFFIX/user/self/profile")
//    fun updateUserProfile(
//        @Part profileImageFile: MultipartBody.Part?,
//        @Part("nickname") nickNameRequestBody: RequestBody?,
//        @Part("gender") genderRequestBody: RequestBody?,
//        @Part("height") heightRequestBody: RequestBody?,
//        @Part("place_id") placeId: RequestBody?,
//        @Part("place_main_text") placeMainText: RequestBody?,
//        @Part("biography") biographyRequestBody: RequestBody?,
//        @Part("profile_site_link") profileSiteLinkRequestBody: RequestBody?,
//        @Part("code") codeRequestBody: RequestBody?
//    ): Completable
//
//    /**
//     * This method used to get username exists
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/user-name/exists")
//    fun getUserNameExits(@Query("username", encoded = true) userName: String): Single<SignUpUserNameResponse>
//
//    /**
//     * This method used to put term-policy
//     */
//    @PUT("v2/$LOCAL_SUFFIX/term-policy/info")
//    fun putTermPolicy(@Body loginTermPolicy: LoginTermPolicy): Single<TermPolicyResponse>
//
//    /**
//     * This method used to get term-policy
//     */
//    @GET("v2/$LOCAL_SUFFIX/term-policy/info")
//    fun getTermPolicy(): Single<TermPolicyResponse>
//
//    /**
//     * This method used to accept term of use.
//     */
//    @POST("v2/$LOCAL_SUFFIX/login/term-accept")
//    fun acceptTermsOfUse(@Body loginTokenRequest: LoginTokenRequest): Single<SignUpAccessTokenResponse>
//
//    /**
//     * This method used to get user information
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/self/profile")
//    fun getUserInformation(): Single<SignUpUserResponse>
//
//    /**
//     * This method used to get current authenticated user
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/self/profile")
//    fun getCurrentUserProfile(): Single<CurrentUser>
//
//    /**
//     * This method used get list location when user search location
//     */
//    @GET("v1/$LOCAL_SUFFIX/location/search")
//    fun getListLocationSearch(
//        @Query("location_key_word", encoded = true) locationKeyWord: String,
//        @Query("result_limit", encoded = true) resultLimit: Int,
//        @Query("accept_language", encoded = true) language: String
//    ): Single<LocationResponse>
//
//    /**
//     * This method used to get language.
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/language")
//    fun getLanguage(): Single<SignUpLanguageResponse>
//
//    /**
//     * This method used to get region.
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/region")
//    fun getRegion(): Single<SignUpRegionResponse>
//
//    @POST("v1/$LOCAL_SUFFIX/user/push/token")
//    @FormUrlEncoded
//    fun pushDeviceToken(
//        @Field("token") token: String,
//        @Field("uuid") uuid: String,
//        @Field("os_type") osType: Int = OS_TYPE
//    ): Completable
//
//    /**
//     * This method used to get outfit explore.
//     */
//    @GET("v2/$LOCAL_SUFFIX/outfit/explore")
//    fun getOutfitExplore(
//        @Query("option_gender") optionGender: List<String>,
//        @Query("option_recency") optionRecency: Int,
//        @Query("option_location_country") locationCountry: List<String>?,
//        @Query("min_height") minHeight: Int?,
//        @Query("max_height") maxHeight: Int?,
//        @Query("ranking_manner") rankingManner: String,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<OutfitExploreResponse>
//
//    /**
//     * Get user explore
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/explore")
//    fun getPopularUsers(
//        @Query("option_gender") optionGender: Int,
//        @Query("option_recency") optionRecency: Int,
//        @Query("result_limit") resultLimit: Int,
//        @Query("option_location_country") locationCountry: String,
//        @Query("option_location_level_1") locationLevel1: String,
//        @Query("option_location_locality") locationLocality: String,
//        @Query("page") page: Int
//    ): Single<PopularUserResponse>
//
//    /**
//     * Get popular hash tags on explore page
//     */
//    @GET("v1/$LOCAL_SUFFIX/popular-hashtag/explore")
//    fun getPopularHashTags(
//        @Query("result_limit") limit: Int = 20,
//        @Query("page") page: Int = 1
//    ): Single<ExploreHashTagsResponse>
//
//    /**
//     * Get suggest hash tags on explore page
//     */
//    @GET("v1/$LOCAL_SUFFIX/suggest-hashtag/explore")
//    fun getSuggestHashTags(): Single<ExploreHashTagsResponse>
//
//    @GET("v1/$LOCAL_SUFFIX/direct-message/{id_direct_message}/detail")
//    fun getDirectMessageDetail(
//        @Path("id_direct_message") messageId: Int
//    ): Single<DirectMessage>
//
//    @GET("v1/$LOCAL_SUFFIX/announcement/{id_announcement}/detail")
//    fun getAnnouncementDetail(
//        @Path("id_announcement") announcementId: Int
//    ): Single<AnnouncementDetailResponse>
//
//    @FormUrlEncoded
//    @POST("v2/$LOCAL_SUFFIX/login/instagram")
//    @Headers("client-id: ${BuildConfig.APP_CLIENT_ID}")
//    fun loginByInstagram(@Field("code") code: String): Single<LoginInstagramResponse>
//
//    //TODO feed api
//    /**
//     * This method used to get outfit feed
//     */
//    @GET("v1/$LOCAL_SUFFIX/outfit/feed")
//    fun getOutfitFeedList(
//        @Query("result_limit") resultLimit: Int,
//        @Query("max_feed_datetime") maxFeedDatetime: Long?
//    ): Single<FeedOutfitResponse>
//
//    /**
//     * Get suggestion
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/suggestion")
//    fun getSuggestion(
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<FeedSuggestionResponse>
//
//    /**
//     * This method userd to get item information related the outfit
//     * get tag
//     */
//    @GET("v1/$LOCAL_SUFFIX/outfit/item-tag")
//    fun getItemTag(@Query("id_outfit") idOutfit: String): Single<ItemTagResponse>
//
//    /**
//     * This method used to star up outfit item.
//     *
//     * @param idOutfitBody - outfit ID.
//     */
//    @POST("v1/$LOCAL_SUFFIX/outfit/{id}/star")
//    fun postOutfitStar(@Path("id") id: Int): Single<PostOutfitStarResponse>
//
//    /**
//     * This method used to star down outfit item.
//     *
//     * @param idOutfitBody - outfit ID.
//     */
//    @DELETE("v1/$LOCAL_SUFFIX/outfit/{id}/star")
//    fun deleteOutfitStar(@Path("id") id: Int): Single<PostOutfitStarResponse>
//
//    /**
//     * This method used to get user information by id user app
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/{id_user_app}/profile")
//    fun getUserInformationById(@Path("id_user_app") idUserApp: String): Single<CurrentUser>
//
//    /**
//     * This method used to get badge response
//     */
//    @GET("v1/$LOCAL_SUFFIX/notification/badge")
//    fun getNotificationBadge(): Single<NotificationBadgeResponse>
//
//    /**
//     * This method used to get list notification
//     */
//    @GET("v1/$LOCAL_SUFFIX/notification/info")
//    fun getNotifications(
//        @Query("result_limit") resultLimit: Int,
//        @Query("since_id") sinceId: Int
//    ): Single<NotificationInfoResponse>
//
//    @POST("v1/$LOCAL_SUFFIX/user/{id_user_app}/follow")
//    fun follow(@Path("id_user_app") idUserApp: String): Single<FollowResponse>
//
//    @DELETE("v1/$LOCAL_SUFFIX/user/{id_user_app}/follow")
//    fun unFollow(@Path("id_user_app") idUserApp: String): Single<FollowResponse>
//
//    @POST("v2/$LOCAL_SUFFIX/outfits/{id_outfit}:undelete")
//    fun unDeleteOutfit(@Path("id_outfit") outfitId: Int): Single<OutfitDeleteResponse>
//
//    @GET("v1/$LOCAL_SUFFIX/user/{id_user_app}/outfit/upload")
//    fun getOutfitUploadUser(
//        @Path("id_user_app") idUserApp: String,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<UserProfileOutfitResponse>
//
//    /**
//     * This method used to get user's favorite.
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/{id_user_app}/favorite/search")
//    fun getUserFavorite(
//        @Path("id_user_app") idUserApp: String,
//        @Query("page") page: Int,
//        @Query("result_limit") resultLimit: Int
//    ): Single<FavoriteResponse>
//
//    /**
//     * This method use to get save item
//     */
//    @GET("v1/$LOCAL_SUFFIX/user/saved-items")
//    fun getSavedItems(@Query("result_limit") resultLimit: Int, @Query("page") page: Int): Single<SaveItemsResponse>
//
//    //TODO item detail api
//
//    /**
//     * This method get item detail
//     */
//    @GET("v1/$LOCAL_SUFFIX/item/{id_pf_item}/detail")
//    fun getItemDetail(@Path("id_pf_item") idPfItem: Int): Single<ItemDetail>
//
//    /**
//     * This method used to get a item detail similar outfit list
//     */
//    @GET("v1/$LOCAL_SUFFIX/item/{id_pf_item}/outfit_list")
//    fun getItemDetailSimilarOutfits(
//        @Path("id_pf_item") idPfItem: Int,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<SimilarOutfitResponse>
//
//    /**
//     * This method used to star up item.
//     *
//     * @param id_pf_item
//     */
//    @POST("v1/$LOCAL_SUFFIX/item/{id_pf_item}/star")
//    fun itemDetailStarCountUp(@Path("id_pf_item") id: Int): Single<StarCountResponse>
//
//    /**
//     * This method used to star down item.
//     *
//     * @param id_pf_item
//     */
//    @DELETE("v1/$LOCAL_SUFFIX/item/{id_pf_item}/star")
//    fun itemDetailStarCountDown(@Path("id_pf_item") id: Int): Single<StarCountResponse>
//
//    /**
//     * This method used to get Instagram info.
//     */
//    @GET("v2/$LOCAL_SUFFIX/user/instagram/info")
//    fun getInstagramInfo(): Single<InstagramInfoResponse>
//
//    /**
//     * This method used to get Instagram outfit list. Default count is 50.
//     */
//    @GET("v2/$LOCAL_SUFFIX/user/instagram/outfit")
//    fun getInstagramOutfit(
//        @Query("count") count: String = "50",
//        @Query("max_id") maxId: String = ""
//    ): Single<InstagramOutfitResponse>
//
//    /**
//     * This method used to text search outfit
//     */
//    @GET("v2/${LOCAL_SUFFIX}/item/text-search")
//    fun textSearch(
//        @Query("title", encoded = true) title: String?,
//        @Query("brand_id", encoded = true) brandId: Int?,
//        @Query("brand_name", encoded = true) brandName: String?,
//        @Query("gender", encoded = true) gender: String?,
//        @Query("result_limit", encoded = true) resultLimit: Int,
//        @Query("page", encoded = true) page: Int
//    ): Single<TextSearchResponse>
//
//    @GET("v2/${LOCAL_SUFFIX}/item/text-search")
//    fun searchItem(
//        @Query("title") title: String?,
//        @Query("brand_id") brandIds: List<Int>?,
//        @Query("brand_name") brandNames: List<String>?,
//        @Query("save_history") saveHistory: Boolean? = true,
//        @Query("gender") genders: List<String>?,
//        @Query("color_code") colorCode: String?,
//        @Query("sorting") sorting: String?,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<SearchItemResponse>
//
//    /**
//     * This method used to get brand list
//     */
//    @GET("v2/${LOCAL_SUFFIX}/brands")
//    fun getBrandList(
//        @Query("result_limit", encoded = true) resultLimit: Int,
//        @Query("brand_key_word", encoded = true) brandKeyWord: String
//    ): Single<BrandResponse>
//
//    /**
//     * TODO: needs to delete
//     * This method used to search hashtag by keyword.
//     */
//    @GET("v1/$LOCAL_SUFFIX/outfit/hashtag")
//    fun searchHashtagOld(
//        @Query("hashtag_key_word", encoded = true) keyWord: String,
//        @Query("result_limit", encoded = true) resultLimit: Int
//    ): Single<HashtagResponse>
//
//    /**
//     * Search hashtag by keyword.
//     */
//    @GET("v2/$LOCAL_SUFFIX/outfits/search/hashtag")
//    fun searchHashtag(
//        @Query("hashtag") keyWord: String,
//        @Query("save_history") saveHistory: Boolean? = true,
//        @Query("region") regions: List<String>?,
//        @Query("style_gender") genders: List<Int>?,
//        @Query("min_height") minHeight: Int?,
//        @Query("max_height") maxHeight: Int?,
//        @Query("manual_color_code_top") upColorCode: String?,
//        @Query("manual_color_code_bottom") bottomColorCode: String?,
//        @Query("sorting") sorting: String?,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<SearchOutfitResponse>
//
//    /**
//     * This method used get list location when user search location
//     */
//    @GET("v1/$LOCAL_SUFFIX/location/search")
//    fun getListSuggestLocation(
//        @Query("result_limit", encoded = true) resultLimit: Int,
//        @Query("latitude") latitude: Double,
//        @Query("longitude") longitude: Double,
//        @Query("accept_language", encoded = true) language: String
//    ): Single<LegacyLocationResponse>
//
//    /**
//     * get detail of outfit by a given outfit id.
//     */
//    @GET("v2/$LOCAL_SUFFIX/outfits/{id_outfit}")
//    fun getOutfitDetail(@Path("id_outfit") id: Int): Single<OutfitDetail>
//
//    /**
//     * get tagged-items by a given outfit id.
//     */
//    @GET("v2/$LOCAL_SUFFIX/outfits/{id_outfit}/tagged-items")
//    fun getOutfitDetailTagged(@Path("id_outfit") id: Int): Single<TaggedItems>
//
//    /**
//     * Get similar items at outfit detail page.
//     * */
//    @GET("v2/${LOCAL_SUFFIX}/outfits/{id_outfit}/similar-outfits")
//    fun getOutfitSimilarItems(
//        @Path("id_outfit") id: Int,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<OutfitSimilarResponse>
//
//    /**
//     * This method used to post outfit to server.
//     */
//    @POST("v1/$LOCAL_SUFFIX/outfit/detail")
//    fun postOutfit(@Body outfit: UpdateOutfitBody): Completable
//
//    /**
//     * This method used to update detail outfit
//     */
//    @PATCH("v2/$LOCAL_SUFFIX/outfits/{id_outfit}")
//    fun updateOutfitDetail(@Path("id_outfit") outfitId: String, @Body outfit: UpdateOutfitDetailBody): Completable
//
//    /**
//     * get suggest users list
//     */
//    @GET("v1/$LOCAL_SUFFIX/suggest-users")
//    fun suggestUser(
//        @Query("region") region: String?,
//        @Query("gender") gender: Int,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<SuggestUserResponse>
//
//    /**
//     * This method used to search outfit with texts
//     */
//    @GET("v1/${LOCAL_SUFFIX}/outfit/text-search")
//    fun searchOutfit(
//        @Query("q") keyWord: String,
//        @Query("save_history") saveHistory: Boolean? = true,
//        @Query("region") regions: List<String>?,
//        @Query("style_gender") genders: List<Int>?,
//        @Query("min_height") minHeight: Int?,
//        @Query("max_height") maxHeight: Int?,
//        @Query("manual_color_code_top") upColorCode: String?,
//        @Query("manual_color_code_bottom") bottomColorCode: String?,
//        @Query("sorting") sorting: String?,
//        @Query("result_limit") resultLimit: Int,
//        @Query("page") page: Int
//    ): Single<SearchOutfitResponse>
//
//    /**
//     * This method used to download user profile
//     */
//    @POST("v1/${LOCAL_SUFFIX}/download-requests")
//    fun downLoadRequest(
//        @Body dataDownLoad: DataDownLoad
//    ): Completable
//
//    /**
//     * This method used to get PrivacySettings
//     */
//    @GET("v1/${LOCAL_SUFFIX}/user/privacy-settings")
//    fun getPrivacySettings(): Single<PrivacySettingResponse>
//
//    /**
//     * This method used to set PrivacySettings
//     */
//    @PATCH("v1/${LOCAL_SUFFIX}/user/privacy-settings")
//    fun setPrivacySettings(
//        @Body map: Map<String, Boolean?>
//    ): Single<PrivacySettingPatchResponse>
//
//    /**
//     * This method use get outfit visual search
//     */
//    @POST("v1/${LOCAL_SUFFIX}/outfit/visual-search")
//    fun getOutfitVisualSearch(
//        @Body outfit: OutfitVisualSearchBody,
//        @Query("style_gender") styleGender: Int?
//    ): Single<OutfitVisualSearchResponse>
//
//    @POST("v1/${LOCAL_SUFFIX}/user/device-mapping")
//    fun deviceMapping(
//        @Body requestBody: DeviceMappingRequestBody
//    ): Completable
//
//    @POST("v2/$LOCAL_SUFFIX/register/device")
//    fun loginDevice(@Body loginDeviceRequest: LoginDeviceRequest): Single<LoginResponse>
//
//    @GET("static/version.json")
//    fun getTermsVersion(): Single<Version>
//
//    @GET("v1/$LOCAL_SUFFIX/banners")
//    fun getBanners(): Single<BannerResult>
//
//    @GET("v2/$LOCAL_SUFFIX/items/{id_item}/outfits")
//    fun getMatchItOutfits(
//        @Path("id_item") itemId: Int,
//        @Query("manual_color_code") manualColorCode: String?,
//        @Query("category") category: String?,
//        @Query("page") page: Int,
//        @Query("result_limit") resultLimit: Int,
//        @Query("count_by") countBy: List<String>?
//    ): Single<MatchItOutfitEntity>
//
//    @GET("v1/$LOCAL_SUFFIX/users/profile")
//    fun getUserProfiles(@Query("user_ids") userIds: String): Single<GetUsersProfileResponse>
//
//    @GET("v2/$LOCAL_SUFFIX/outfit-stars")
//    fun getOutfitStars(
//        @Query("id_outfit") idOutfit: List<Int>,
//        @Query("id_user_app") idUserApp: Int,
//        @Query("page") page: Int,
//        @Query("result_limit") resultLimit: Int
//    ): Single<GetOutfitStarsResponse>
//
//    @GET("v1/$LOCAL_SUFFIX/ss-categories")
//    fun getS2SCategories(): Single<GetS2SCategoriesResponse>
//
//    @GET("v1/$LOCAL_SUFFIX/color-palette")
//    fun getColorPalette(): Single<GetColorPaletteResponse>
//
//    @PATCH("v1/$LOCAL_SUFFIX/user/self/profile")
//    fun patchChangUserId(@Body changeUserIDRequest: ChangeUserIDRequest): Completable
//
//    @POST("v1/${LOCAL_SUFFIX}/user-linkage")
//    fun linkUqAccount(
//        @Body requestBody: LinkageRequestBody
//    ): Single<CouponResponse>
//
//    @POST("v1/${LOCAL_SUFFIX}/promotion-coupons")
//    fun promotionCoupons(): Single<CouponResponse>
//
//    @GET("v2/$LOCAL_SUFFIX/outfits/count")
//    fun getOutfitsCount(
//        @Query("option_location_country") locationCountry: List<String>?,
//        @Query("option_gender") optionGender: List<Int>,
//        @Query("option_recency") optionRecency: Int,
//        @Query("min_height") minHeight: Int?,
//        @Query("max_height") maxHeight: Int?
//    ): Single<FilterOutfitsCountEntity>
//
//    /**
//     * Get Reason Delete Account
//     */
//    @GET("v1/${LOCAL_SUFFIX}/selection")
//    fun getSelection(@Query("code_type") codeType: Int): Single<SelectionResponse>
//
//    /**
//     * Get Outfit star users list
//     */
//    @GET("v1/${LOCAL_SUFFIX}/outfits/{id_outfit}/star-users")
//    fun getOutfitStarUsers(
//        @Path("id_outfit") outfitId: Int,
//        @Query("page") page: Int,
//        @Query("result_limit") resultLimit: Int
//    ): Single<OutfitStarUsersResponse>
//
//    @GET("v1/${LOCAL_SUFFIX}/keyword-history")
//    fun getInitialPageHistory(@Query("result_limit") resultLimit: Int): Single<InitialPageHistoryEntity>
//
//    @GET("v1/${LOCAL_SUFFIX}/popular-keywords")
//    fun getInitialPageTrending(): Single<InitialPageTrendingEntity>
//
//    @DELETE("v1/${LOCAL_SUFFIX}/keyword-history")
//    fun deleteInitialPageHistory(): Completable
//
//    @GET("/v2/${LOCAL_SUFFIX}/users/basic")
//    fun getInitialPageUsers(@Query("user_ids") userIds: String): Single<InitialPageTrendingUserEntity>
//
//    @GET("v2/${LOCAL_SUFFIX}/outfits/count/text-search")
//    fun getOutfitsSearchCount(
//        @Query("q") query: String,
//        @Query("region") regions: List<String>?,
//        @Query("style_gender") genders: List<Int>?,
//        @Query("min_height") minHeight: Int?,
//        @Query("max_height") maxHeight: Int?,
//        @Query("manual_color_code_top") topColorCode: String?,
//        @Query("manual_color_code_bottom") bottomColorCode: String?
//    ): Single<SearchFilterResponse>
//
//    @GET("v2/${LOCAL_SUFFIX}/text-search/suggestion")
//    fun getTextSearchSuggestion(
//        @Query("q") query: String,
//        @Query("search_category") category: Int?
//    ): Single<SearchSuggestionResponse>
//
//    @GET("v1/${LOCAL_SUFFIX}/user/text-search")
//    fun getSuggestionsUsers(
//        @Query("q") query: String,
//        @Query("result_limit") resultLimit: Int?,
//        @Query("page") page: Int?
//    ): Single<TextSearchSuggestionUserListResponse>
//
//    @PUT("v1/${LOCAL_SUFFIX}/keyword-history")
//    fun putKeyWordHistory(
//        @Body putKeywordHistory: PutKeywordHistory
//    ): Completable
//
//    @GET("v2/${LOCAL_SUFFIX}/items/count/text-search")
//    fun getItemsSearchCount(
//        @Query("title") query: String,
//        @Query("brand_name") brandNames: List<String>?,
//        @Query("brand_id") brandIds: List<Int>?,
//        @Query("gender") genders: List<String>?
//    ): Single<SearchFilterResponse>
//
//    @GET("v2/${LOCAL_SUFFIX}/outfits/count/search/hashtag")
//    fun getHashTagsSearchCount(
//        @Query("hashtag") query: String,
//        @Query("region") regions: List<String>?,
//        @Query("style_gender") genders: List<Int>?,
//        @Query("min_height") minHeight: Int?,
//        @Query("max_height") maxHeight: Int?,
//        @Query("manual_color_code_top") topColorCode: String?,
//        @Query("manual_color_code_bottom") bottomColorCode: String?
//    ): Single<SearchFilterResponse>
//
//    @GET("v1/${LOCAL_SUFFIX}/remote-config")
//    fun getAdminRemoteConfig(
//        @Query("key") key: String?
//    ): Flow<List<ConfigEntity>>
//
//    /**
//     * This method used to delete outfit
//     */
//    @DELETE("v2/${LOCAL_SUFFIX}/outfits/{id_outfit}")
//    fun deleteOutfit(@Path("id_outfit") outfitId: Int): Single<OutfitDeleteResponse>
//
//    @GET("v5/${LOCAL_SUFFIX}/items/pickup")
//    fun getPickUpItems(@Query("page") page: Int?): Flow<PickUpItemsResponse>
//
//    /**
//     * Get video collections
//     */
//    @GET("v1/$LOCAL_SUFFIX/outfit-collection-groups/{outfit_collection_group_key}")
//    fun getVideoCollections(@Path("outfit_collection_group_key") collectionGroupKey: String): Flow<VideoCollectionsResponse>
//
//    /**
//     * Get outfits of single collection
//     */
//    @GET("v1/$LOCAL_SUFFIX/outfit-collections/{collectionKey}")
//    fun getCollectionOutfits(@Path("collectionKey") collectionKey: String): Flow<CollectionOutfitsResponse>
//
//    /**
//     * Get the list of items with id_pf_item column by product id
//     */
//    @GET("v2/${LOCAL_SUFFIX}/pf-items")
//    fun getPfItemsByProductId(@Query("product_id") productId: String): Flow<PfItemsResponse>
//
//    /**
//     * get suggest item list for tag item detail
//     */
//    @GET("v5/${LOCAL_SUFFIX}/items/suggest-tagged-items")
//    fun getSuggestItem(
//        @Query("brand_name") brandName: String?,
//        @Query("gender") gender: String?,
//        @Query("page") page: Int = 1,
//        @Query("result_limit") resultLimit: Int = 50
//    ): Single<GetSuggestItemResponse>
//
//    /**
//     * get recommendation list
//     */
//    @GET("v5/${LOCAL_SUFFIX}/users/suggestion")
//    fun getRecommendationList(
//        @Query("region") region: String?,
//        @Query("gender") gender: Int?,
//        @Query("page") page: Int = 1,
//        @Query("result_limit") resultLimit: Int = 20
//
//    ): Single<RecommendationResponse>
}
