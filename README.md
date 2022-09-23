# StackSpot IntelliJ Plugin

## **Table of contents**

### 1. [**About**](#about)
### 2. [**Getting Started**](#getting-started)
>#### 2.1. [**Build Requirements**](#build-requirements)
>#### 2.2. [**Gradle Commands**](#gradle-commands)
### 3. [**Installation**](#installation)
### 4. [**Usage**](#usage)
### 5. [**Documentation**](#documentation)
### 6. [**Contributing**](#contributing)
### 7. [**Code of Conduct**](#code-of-conduct)
### 8. [**License**](#license)
### 9. [**Other Information**](#other-information)
>#### 9.1. [**IntelliJ Plugin SDK documentation**](#intellij-plugin-sdk-documentation)

## **About**

**StackSpot IntelliJ Plugin** is a plugin that extends [STK CLI](https://docs.stackspot.com/latest/docs/stk-cli/) to IntelliJ and allows you to create a StackSpot project, apply plugins, and many more features. But all this is inside of your favorite IDE.

## **Getting started**

### **Build Requirements**

- IntelliJ IDEA 2022.1
- JDK 11

### **Gradle Commands**

Always use `./gradlew clean` to be sure that the change you made are in the last binary.

To build plugin to install in your IDE use:

```
./gradlew clean buildPlugin
```

The zip file will be in `build/distributions/{plugin_name}-{version}.zip`

To run your code use:

```
./gradlew clean runIde
```

## **Installation**

We will let this plugin available in JetBrains Marketplace, but if you want to install through the zip file check this:
[IntelliJ install plugin from the disk](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk)

## **Usage**

Create a new project (Project Wizard):

<p align="center">
  <a href="https://gifyu.com/image/STIbO"><img src="https://s4.gifyu.com/images/create-project-project-wizard.gif" alt="gif containing the creation of a project" border="0" /></a>
</p>

Using StackSpot tool window after project opened:

<p align="center">
  <a href="https://gifyu.com/image/STIzK"><img src="https://s4.gifyu.com/images/overview-ide.gif" alt="gif containing the StackSpot tool window" border="0" /></a>
</p>

## **Documentation**

Check our official documentation page to use StackSpot IntelliJ plugin:
[Documentation link](https://docs.stackspot.com/latest/docs/extensions-for-ide/intellij/)

## **Contributing**

Check out our [**Contributing Guide**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/CONTRIBUTING.md) to learn about our development process, how to suggest bug fixes and improvements.

Check out other guides:

- [**Security**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/SECURITY.md)

- [**Developer Guide**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/DEVELOPER_GUIDE.md)

## **Code of Conduct**
Please follow the [**Code of Conduct**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/CODE_OF_CONDUCT.md) in all your interactions with our project.

## **License**
[**Apache License 2.0**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/LICENSE).

## **Other information**

### **IntelliJ Plugin SDK documentation**

- https://plugins.jetbrains.com/docs/intellij/welcome.html

