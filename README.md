# 17780_hw2_API_doc

## For Part 2:
In this repo, `java/net_modified/HttpURLConnection.java` is modified. Only the Javadoc of this file is modified, please ignore the function's body since some of them are deleted to remove the compile errors. There will be some "duplicated module" issues if the whole JDK is cloned here, so I just keep `HttpURLConnection.java` and add the necessary classes in `/net`. These empty classes in `/net` are also used to remove compile errors.

In short, only pay attention to the documentation of `java/net_modified/HttpURLConnection.java`. Please ignore the implementation and other files.

### Steps to do
1. Modify the documentation in `java/net_modified/HttpURLConnection.java`
2. `cd java/net_modified`
3. `javadoc -d ./htmls HttpURLConnection.java` to generate the HTML file for Javadoc
4. Enter the `/htmls` folder -> right click on index.html -> open with live server in VSCode.


## For Part 3:
Feel free to make changes to:
1. `Thermometer.java`, which is the API.
2. `DigitalThermometer.java`, which is an easy implementation of the API above.
3. `Main.java`, which is the test case.

Same as before, to generate a html Javadoc, enter `/part3` and use the `javadoc -d ./htmls Thermometer.java` command.


