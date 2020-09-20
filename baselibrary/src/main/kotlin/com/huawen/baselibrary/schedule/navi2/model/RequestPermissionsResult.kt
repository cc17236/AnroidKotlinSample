/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawen.baselibrary.schedule.navi2.model

import com.google.auto.value.AutoValue
import java.util.Arrays
import java.util.Collections

@AutoValue
abstract class RequestPermissionsResult {

    abstract fun requestCode(): Int

    abstract fun permissions(): List<String>

    abstract fun grantResults(): IntArray

    companion object {

        fun create(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
        ): RequestPermissionsResult {
            return AutoValue_RequestPermissionsResult(
                requestCode,
                Collections.unmodifiableList(Arrays.asList(*permissions)), grantResults
            )
        }
    }
}
