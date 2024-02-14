package dev.xinto.argos.network.response

import kotlinx.serialization.Serializable

interface ApiResponseBase {
    val id: String?
    val errors: ApiErrors?
    val message: String
}

@Serializable
data class ApiResponse<D>(
    override val id: String? = null,
    val data: D? = null,
    override val errors: ApiErrors? = null,
    override val message: String,
) : ApiResponseBase

typealias ApiResponseEmpty = ApiResponse<ApiResponseData<List<Nothing>>>

@Serializable
data class ApiResponseWithMeta<D, M>(
    override val id: String? = null,
    val data: D? = null,
    val meta: M? = null,
    override val errors: ApiErrors? = null,
    override val message: String,
) : ApiResponseBase

typealias ApiResponsePaged<D> = ApiResponseWithMeta<D, ApiResponsePagingMeta>

@Serializable
data class ApiErrors(
    val general: List<String>
)

@Serializable
data class ApiResponseData<A>(
    val id: String? = null,
    val attributes: A,
    val type: String
)

@Serializable
data class ApiResponseDataWithRelationships<A, R>(
    val id: String? = null,
    val attributes: A,
    val relationships: R,
    val type: String,
)

@Serializable
data class ApiResponseDataWithPossibleRelationships<A, R>(
    val id: String? = null,
    val attributes: A,
    val relationships: R? = null,
    val type: String,
)

@Serializable
data class ApiResponseRelationship<D>(
    val data: D
)

@Serializable
data class ApiResponsePagingMeta(
    val pagination: ApiResponsePagingAttributes
)

@Serializable
data class ApiResponsePagingAttributes(
    val total: Int,
    val count: Int,
    val perPage: Int,
    val currentPage: Int,
    val totalPages: Int,
    val links: ApiResponsePagingLinks
)

@Serializable
data class ApiResponsePagingLinks(
    val next: String?,
    val previous: String?,
)