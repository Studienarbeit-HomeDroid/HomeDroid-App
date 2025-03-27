import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IconResponse(
    @SerialName("total_count") val totalCount: Int,
    val icons: List<Icon>
)

@Serializable
data class Icon(
    @SerialName("icon_id") val iconId: Int,
    val tags: List<String>,
    @SerialName("published_at") val publishedAt: String,
    @SerialName("is_premium") val isPremium: Boolean,
    val type: String,
    val containers: List<Container>,
    @SerialName("raster_sizes") val rasterSizes: List<RasterSize>,
    @SerialName("vector_sizes") val vectorSizes: List<VectorSize>,
    val styles: List<Style>,
    val categories: List<Category>,
    @SerialName("is_icon_glyph") val isIconGlyph: Boolean
)

@Serializable
data class Container(
    val format: String,
    @SerialName("download_url") val downloadUrl: String
)

@Serializable
data class RasterSize(
    val formats: List<FormatRaster>,
    val size: Int,
    @SerialName("size_width") val sizeWidth: Int,
    @SerialName("size_height") val sizeHeight: Int
)

@Serializable
data class FormatRaster(
    val format: String,
    @SerialName("preview_url") val previewUrl: String,
    @SerialName("download_url") val downloadUrl: String
)

@Serializable
data class FormatVektor(
    val format: String,
    @SerialName("download_url") val downloadUrl: String
)

@Serializable
data class VectorSize(
    val formats: List<FormatVektor>,
    @SerialName("target_sizes") val targetSizes: List<List<Int>>,
    val size: Int,
    @SerialName("size_width") val sizeWidth: Int,
    @SerialName("size_height") val sizeHeight: Int
)

@Serializable
data class Style(
    val identifier: String,
    val name: String
)

@Serializable
data class Category(
    val identifier: String,
    val name: String
)