package com.android.sample.model.userAccount

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test

class UserAccountLocalCacheTest {

  private lateinit var localCache: UserAccountLocalCache
  private val userAccount =
      UserAccount(
          userId = "1",
          firstName = "John",
          lastName = "Doe",
          height = 180f,
          weight = 75f,
          profileImageUrl = "profile_image_url")

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    localCache = UserAccountLocalCache(context)
  }

  @Test
  fun getUserAccount_returns_cached_data() = runTest {
    localCache.saveUserAccount(userAccount)

    val cachedAccount = localCache.getUserAccount().first()

    assertThat(cachedAccount, `is`(userAccount))
  }

  @Test
  fun clearUserAccount_removes_cached_data() = runTest {
    localCache.saveUserAccount(userAccount)
    localCache.clearUserAccount()

    val cachedAccount = localCache.getUserAccount().first()
    assertThat(cachedAccount, `is`(nullValue()))
  }
}
