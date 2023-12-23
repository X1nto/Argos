package dev.xinto.argos.network.response.attributes

import kotlinx.serialization.Serializable

@Serializable
data class ApiAttributesCourseSyllabus(
    val duration: String,
    val hours: String,
    val lecturers: String,
    val prerequisites: String,
    val methods: String,
    val mission: String,
    val topics: String,
    val outcomes: String,
    val evaluation: String,
    val resources: String,
    val otherResources: String,
    val schedule: String,
    val knowledge: Boolean,
    val applying: Boolean,
    val judgments: Boolean,
    val communications: Boolean,
    val learning: Boolean,
    val values: Boolean
)
