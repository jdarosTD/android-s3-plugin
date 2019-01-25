# android-s3-plugin


# Setup : Add the plugin to your project 
Build script snippet for plugins DSL for Gradle 2.1 and later:
```groovy

plugins {
  id("com.ins.gradle.plugin.android.s3") version "0.1"
}

```
Build script snippet for use in older Gradle versions or where dynamic configuration is required:
```groovy
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("gradle.plugin.com.ins.gradle.plugin.android:android-s3-plugin:0.1")
  }
}

apply(plugin = "com.ins.gradle.plugin.android.s3")


```

# Authentication

The S3 plugin searches for credentials in the same order as the [AWS default credentials provider chain](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html). Additionally you can specify a credentials profile to use by setting the project `uploadS3.profile` property:

```groovy
uploadS3 {
    profile = 'my-profile'
}
```

Setting the environment variables `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` is one way to provide your S3 credentials. See the [AWS Docs](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html) for details on credentials.


# Config

Other properties available:

    bucket    - S3 bucket to use (optional, defaults to the project s3 configured bucket)
    dest      - optional path for the uploaded package 


# Usage

Applying this plugin offers as many upload tasks as application variants available and respecting the template "upload${variantName}Apks"

## Example :

Configuration
```kotlin 
productFlavors {
        dev {
            dimension "flavorDimension"
            applicationIdSuffix ".dev"
            
            ...
        }
    
        prod {
            dimension "flavorDimension"
            ...
        }
 }
    
 buildTypes {
      release {
         ...
      }
      debug { 
        ...
      }
  }
    
```

This APP configuration offers 2 flavors X 2 builds type so 4 variants and tasks : 
  ```
    uploadDevDebugApks
    uploadDevReleaseApks
    uploadProdDebugApks
    uploadProdReleaseApks
  ```

In addition you can decide to upload all of your variants with a single one

```
  uploadAllApks
```

 ## Output Format 
    
    the template for the file name uploaded is :
    
    ${baseApplicationId}${applicationSuffix}_${baseVersion}${versionSuffix}.apk


# NOTA BENE 
  ## On Debug mode ONLY
      The generated apks are overwritten on Amazon S3
 
# Known Issue

It is possible to apply a "split" configuration to your app to seperate the different resources.  Example :

```groovy

    splits {

        density {
            enable true
            exclude "ldpi"
            compatibleScreens 'small', 'normal', 'large', 'xlarge'
        }
    }

```

The android-s3-plugin is trying to uploading the files but the final apk full path destination does not include the screen density as it is the case in the one locally generated 

Example : app-dev-xxhdpi-debug.apk locally but final is 1.0-dev-debug


