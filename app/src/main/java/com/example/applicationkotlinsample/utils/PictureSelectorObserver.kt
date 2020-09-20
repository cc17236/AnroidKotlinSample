package cn.aihuaiedu.school.utils

import android.app.Activity
import cn.aihuaiedu.school.base.RxPresenter
import com.example.applicationkotlinsample.R
import com.huawen.baselibrary.alias.PictureSelectorReplace
import com.huawen.baselibrary.startRxActivityForResult
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.PictureSelectorActivity
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.DoubleUtils

object PictureSelectorObserver {
    fun create(presenter: Any, function: (results:List<String>) -> Unit, selectNum: Int? = null, camera: Boolean? = null, crop: Boolean? = null, square: Boolean? = false) {
        // 进入相册 以下是例子：用不到的api可以不写
        val activity: Activity = if (presenter is Activity) presenter else (presenter as RxPresenter<*>).getContext() as Activity
        val selector = PictureSelectorReplace.create(activity)
        val ab = selector.openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(selectNum ?: 1)// 最大图片选择数量 int

        if ((selectNum ?: 1) == 1) {
            ab.selectionMode(PictureConfig.SINGLE)
        } else {
            ab.selectionMode(PictureConfig.MULTIPLE)
        }

        ab.minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(camera ?: false)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效

//                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
        ab.enableCrop(crop ?: false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
        if (square == true) {
            ab.withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1.可自定义
        } else {
            ab.withAspectRatio(16, 9)// int 裁剪比例 如16:9 3:2 3:4 1:1.可自定义
        }
        //                            .hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
        ab
//                .compressSavePath(PictureFileUtils.getPath(presenter.getContext() as Context,
//                Uri.fromFile(FileUtil.createTmpFile(presenter.getContext()!!))))//压缩图片保存地址
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .minimumCompressSize(500)// 小于500kb的图片不压缩
                .synOrAsy(false)//同步true或异步false 压缩 默认同步
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .outlet()
        if (!DoubleUtils.isFastDoubleClick()) {
            activity.startRxActivityForResult<PictureSelectorActivity>() { intent, resultCode ->
                val selectList = PictureSelector.obtainMultipleResult(intent)
                if (selectList != null && (selectList.size > 0)) {
                    val list = arrayListOf<String>()
                    for (i in 0 until selectList.size) {
                        val media = selectList[i]
                        var path: String?
                        if (media.isCut) {
                            if (media.isCompressed) {
                                path = media.compressPath
                            } else {
                                path = media.cutPath
                            }
                        } else if (media.isCompressed) {
                            path = media.compressPath
                        } else {
                            path = media.path
                        }
                        list.add(path)
                    }
                    function.invoke(list)
                }
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
            }

        }
//            presenter.startActivityForResult<PictureSelectorActivity>({
//                val selectList = PictureSelector.obtainMultipleResult(it)
//                if (selectList != null && (selectList.size > 0)) {
//                    val list = arrayListOf<String>()
//                    for (i in 0 until selectList.size) {
//                        val media = selectList[i]
//                        var path: String?
//                        if (media.isCut) {
//                            if (media.isCompressed) {
//                                path = media.compressPath
//                            } else {
//                                path = media.cutPath
//                            }
//                        } else if (media.isCompressed) {
//                            path = media.compressPath
//                        } else {
//                            path = media.path
//                        }
//                        list.add(path)
//                    }
//                    function.invoke(list)
//                }
//                // 例如 LocalMedia 里面返回三种path
//                // 1.media.getPath(); 为原图path
//                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
//                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
//                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//            })
        selector.getActivity()?.overridePendingTransition(R.anim.a5, 0)
    }

    fun createVideoPhoto(presenter: Any, function: (List<String>, hasVideo: Boolean) -> Unit, selectNum: Int? = null, camera: Boolean? = null, crop: Boolean? = null, square: Boolean? = false) {
        // 进入相册 以下是例子：用不到的api可以不写
        val activity: Activity = if (presenter is Activity) presenter else (presenter as RxPresenter<*>).getContext() as Activity
        val selector = PictureSelectorReplace.create(activity)

        val ab = selector.openGallery(PictureMimeType.ofAll())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(selectNum ?: 1)// 最大图片选择数量 int
                .selectionMode(PictureConfig.SINGLE)

        if ((selectNum ?: 1) == 1) {
            ab.selectionMode(PictureConfig.SINGLE)
        } else {
            ab.selectionMode(PictureConfig.MULTIPLE)
        }

        ab.minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(camera ?: false)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效

//                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
        ab.enableCrop(crop ?: false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
        if (square == true) {
            ab.withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1.可自定义
        } else {
            ab.withAspectRatio(16, 9)// int 裁剪比例 如16:9 3:2 3:4 1:1.可自定义
        }
        //                            .hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
        ab
//                .compressSavePath(PictureFileUtils.getPath(presenter.getContext() as Context,
//                Uri.fromFile(FileUtil.createTmpFile(presenter.getContext()!!))))//压缩图片保存地址
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .minimumCompressSize(500)// 小于500kb的图片不压缩
                .previewVideo(false)
                .synOrAsy(false)//同步true或异步false 压缩 默认同步
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .outlet()
        if (!DoubleUtils.isFastDoubleClick()) {
            activity.startRxActivityForResult<PictureSelectorActivity>() { intent, resultCode ->
                val selectList = PictureSelector.obtainMultipleResult(intent)
                if (selectList != null && (selectList.size > 0)) {
                    val list = arrayListOf<String>()
                    var hasVideo = false
                    for (i in 0 until selectList.size) {
                        val media = selectList[i]
                        var path: String?
                        if (media.mimeType != PictureMimeType.ofImage() && media.duration > 0) {
                            hasVideo = true
                            path = media.path
                        } else {
                            if (media.isCut) {
                                if (media.isCompressed) {
                                    path = media.compressPath
                                } else {
                                    path = media.cutPath
                                }
                            } else if (media.isCompressed) {
                                path = media.compressPath
                            } else {
                                path = media.path
                            }
                        }
                        list.add(path)
                    }
                    function.invoke(list, hasVideo)
                }
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
            }

        }
//            presenter.startActivityForResult<PictureSelectorActivity>({
//                val selectList = PictureSelector.obtainMultipleResult(it)
//                if (selectList != null && (selectList.size > 0)) {
//                    val list = arrayListOf<String>()
//                    for (i in 0 until selectList.size) {
//                        val media = selectList[i]
//                        var path: String?
//                        if (media.isCut) {
//                            if (media.isCompressed) {
//                                path = media.compressPath
//                            } else {
//                                path = media.cutPath
//                            }
//                        } else if (media.isCompressed) {
//                            path = media.compressPath
//                        } else {
//                            path = media.path
//                        }
//                        list.add(path)
//                    }
//                    function.invoke(list)
//                }
//                // 例如 LocalMedia 里面返回三种path
//                // 1.media.getPath(); 为原图path
//                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
//                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
//                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//            })
        selector.getActivity()?.overridePendingTransition(R.anim.a5, 0)
    }


    fun create(presenter: Any, medias: List<LocalMedia>, function: (origin:List<LocalMedia>,paths:List<String>) -> Unit, selectNum: Int? = null,
               camera: Boolean? = null, crop: Boolean? = null) {
        // 进入相册 以下是例子：用不到的api可以不写
        val activity: Activity = if (presenter is Activity) presenter else (presenter as RxPresenter<*>).getContext() as Activity
        val selector = PictureSelectorReplace
            .create(activity)
            .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .maxSelectNum(selectNum ?: 1)// 最大图片选择数量 int
            .minSelectNum(1)// 最小选择数量 int
            .imageSpanCount(4)// 每行显示个数 int
            .previewImage(true)// 是否可预览图片 true or false
            .isCamera(camera ?: false)// 是否显示拍照按钮 true or false
            .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
            .enableCrop(crop ?: false)// 是否裁剪 true or false
            .compress(true)// 是否压缩 true or false
            .selectionMedia(medias)
            .withAspectRatio(16, 9)// int 裁剪比例 如16:9 3:2 3:4 1:1.可自定义
            //                            .hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
//                .compressSavePath(PictureFileUtils.getPath(presenter.getContext() as Context,
//                        Uri.fromFile(FileUtil.createTmpFile(presenter.getContext()!!))))//压缩图片保存地址
            .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
            .circleDimmedLayer(false)// 是否圆形裁剪 true or false
            .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
            .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
            .openClickSound(false)// 是否开启点击声音 true or false
            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
            .minimumCompressSize(500)// 小于500kb的图片不压缩
            .synOrAsy(false)//同步true或异步false 压缩 默认同步
            .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
            .outlet()
        if (!DoubleUtils.isFastDoubleClick()) {
            activity.startRxActivityForResult<PictureSelectorActivity>() { intent, resultCode ->
                val selectList = PictureSelector.obtainMultipleResult(intent)
                if (selectList != null && selectList.size > 1) {
                    for (i in 0 until selectList.size) {
                        val media = selectList[i]
                        var path: String?
                        if (media.isCut) {
                            if (media.isCompressed) {
                                path = media.compressPath
                            } else {
                                path = media.cutPath
                            }
                        } else if (media.isCompressed) {
                            path = media.compressPath
                        } else {
                            path = media.path
                        }
                        media.path = path
                    }
                    function.invoke(selectList, arrayListOf<String>().apply{
                        selectList.forEach {
                            this.add(it.path)
                        }
                    })
                }
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的


            }
//            presenter.startActivityForResult<PictureSelectorActivity>({
//                val selectList = PictureSelector.obtainMultipleResult(it)
//                if (selectList != null && selectList.size > 1) {
//                    for (i in 0 until selectList.size) {
//                        val media = selectList[i]
//                        var path: String?
//                        if (media.isCut) {
//                            if (media.isCompressed) {
//                                path = media.compressPath
//                            } else {
//                                path = media.cutPath
//                            }
//                        } else if (media.isCompressed) {
//                            path = media.compressPath
//                        } else {
//                            path = media.path
//                        }
//                        media.path = path
//                    }
//                    function.invoke(selectList)
//                }
//                // 例如 LocalMedia 里面返回三种path
//                // 1.media.getPath(); 为原图path
//                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
//                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
//                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//            })
            selector!!.getActivity()?.overridePendingTransition(R.anim.a5, 0)

        }
    }
}

