# 17780_hw2_API_doc
In this repo, `java/net_modified/HttpURLConnection.java` is modified. Only the Javadoc of this file is modified, please ignore the function's body since some of them are deleted to remove the compile errors. There will be some "duplicated module" issues if the whole JDK is cloned here, so I just keep `HttpURLConnection.java` and add the necessary classes in `/net`. These empty classes in `/net` are also used to remove compile errors.

In short, only pay attention to the documentation of `java/net_modified/HttpURLConnection.java`. Please ignore the implementation and other files.

## Steps to do
1. Modify the documentation in `java/net_modified/HttpURLConnection.java`
2. `cd java/net_modified`
3. `javadoc -d ./htmls HttpURLConnection.java` to generate the HTML file for Javadoc
4. Enter the `/htmls` folder -> right click on index.html -> open with live server in VSCode.


