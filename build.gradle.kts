// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.1.0" apply false
    kotlin("android") version "2.2.10" apply false
    kotlin("plugin.serialization") version "2.2.10" apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false
}