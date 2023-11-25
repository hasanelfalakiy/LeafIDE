package io.github.caimucheng.leaf.ide.model

import org.json.JSONObject

data class Project(
    val name: String,
    val description: String,
    val plugin: Plugin,
    val workspace: JSONObject
)