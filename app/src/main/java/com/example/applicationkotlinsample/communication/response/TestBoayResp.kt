package com.example.applicationkotlinsample.communication.response

import cn.aihuaiedu.school.base.entity.ListBaseInfo

data class TestBodyResp(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
): ListBaseInfo<TestBodyResp>()