# **StackSpot IntelliJ Plugin**

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

**StackSpot IntelliJ Plugin** extends [**STK CLI**](https://docs.stackspot.com.br/docs/stk-cli/) to IntelliJ and allows you to create a StackSpot project, apply plugins, and many more features.

## **Getting started**

### **Build Requirements**

- IntelliJ IDEA 2022.1
- JDK 11

### **Gradle Commands**

You need to use **`./gradlew clean`** to make sure the changes you made are in the last binary. Now, follow:

1. Build a plugin to install in your IDE, run:

```
./gradlew clean buildPlugin
```

The zip file will be in `build/distributions/{plugin_name}-{version}.zip`

2. Run your code:

```
./gradlew clean runIde
```

## **Installation**

This plugin is available on JetBrains Marketplace, but you can also install it through the zip file, see below:
- [**Install IntelliJ plugin from the disk**](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk)

## **Usage**

Create a new project (Project Wizard):

<p align="center">
  <a href="https://gifyu.com/image/STIbO"><img src="https://s4.gifyu.com/images/create-project-project-wizard.gif" alt="gif containing the creation of a project" border="0" /></a>
</p>

Using StackSpot tool window after the project opened:

<p align="center">
  <a href="https://gifyu.com/image/STIzK"><img src="https://s4.gifyu.com/images/overview-ide.gif" alt="gif containing the StackSpot tool window" border="0" /></a>
</p>

## **Documentation**

See StackSpot's documentation to learn more about IntelliJ's plugin:
- [**IntelliJ**](https://docs.stackspot.com.br/docs/extensions-for-ide/intellij/)

## **Contributing**

Check out our [**Contributing Guide**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/CONTRIBUTING.md) to learn about our development process, how to suggest bug fixes and improvements.

See other guides:

- [**Security**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/SECURITY.md)

- [**Developer Guide**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/DEVELOPER_GUIDE.md)

## **Code of Conduct**

Please follow the [**Code of Conduct**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/CODE_OF_CONDUCT.md) in all your interactions with our project.

## **License**

[**Apache License 2.0**](https://github.com/stack-spot/stackspot-intellij-extension/blob/main/LICENSE).

## **Other information**

- [**IntelliJ Plugin SDK documentation**](https://plugins.jetbrains.com/docs/intellij/welcome.html)
