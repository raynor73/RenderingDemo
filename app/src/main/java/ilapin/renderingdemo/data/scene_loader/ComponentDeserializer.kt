package ilapin.renderingdemo.data.scene_loader

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * @author raynor on 21.01.20.
 */
class ComponentDeserializer : JsonDeserializer<ComponentDto> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ComponentDto? {
        val jsonObject = json.asJsonObject
        return jsonObject["type"]?.let {
            when (it.asString) {
                "DirectionalLight" -> context.deserialize(jsonObject, ComponentDto.DirectionalLightDto::class.java)
                "MeshRenderer" -> context.deserialize(jsonObject, ComponentDto.MeshRendererDto::class.java)
                "Camera" -> context.deserialize(jsonObject, ComponentDto.CameraDto::class.java)
                else -> null
            }
        }
    }
}