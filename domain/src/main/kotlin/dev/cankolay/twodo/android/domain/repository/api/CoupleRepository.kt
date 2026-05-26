package dev.cankolay.twodo.android.domain.repository.api

import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.user.Couple

interface CoupleRepository {
    suspend fun getMe(): ApiResult<Couple?>
    suspend fun leave(): ApiResult<Nothing?>
}
