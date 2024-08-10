package dev.xinto.argos.domain.user

import dev.xinto.argos.domain.combine

val UserRepository.meUserInfoAndStateFlow
    get() = combine(meUserInfo.flow, meUserState.flow, ::Pair)