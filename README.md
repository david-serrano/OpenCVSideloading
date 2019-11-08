# OpenCVSideloading

Load as an independent binary and dex file that can be extracted from a zip &
stored in the external storage dir

In order to run this example:

- Edit code as needed in the "dynamicmodule2" dependency
- Make the module (Build > make dynamicmodule2)
- Find and run the "CopyDexToApp" gradle task
- Run the app

The copy code runs under a requires android O target API, this needs to be changed to work with lower APIs.
At the moment the app expects to have an "libopencv_java3.so" file under the internal storage directory.
It will the copy the binary to its internal directory and load OpenCV.

Does not account for chipset, dumbly loads x86 for now.


original repository code can be found here - but needs tweaking to work:
https://github.com/davethomas11/AndroidHotSwapCodeExample