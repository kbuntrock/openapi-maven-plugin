# Contributing to OpenAPI Maven Plugin

First off, thank you for considering contributing! 🙌  
Contributions help make this project better for everyone.

We welcome all kinds of contributions:
- 🐛 Bug reports
- 💡 Feature requests
- 🛠️ Code improvements
- 📝 Documentation updates

---
## 📌 How to Contribute
**1. Fork & Clone**  
```
git clone git@github.com:kbuntrock/openapi-maven-plugin.git
cd openapi-maven-plugin
```
**2. Create a Branch**
```
git checkout -b feature/my-awesome-feature
```
**3. Make Changes**
  - The source code of the plugin stricly speaking is located in the `openapi-maven-plugin` sub-folder
  - Follow the Coding Guidelines (see below)
  - Keep commits atomic and messages clear

**4. Run Tests**  
We use Maven for build and test automation:
```
# In the openapi-maven-plugin sub-folder:
mvn clean verify
```

**5. Submit a Pull Request**

- Push your branch and open a PR against the ``dev`` branch.
- Make sure your PR description explains what and why clearly.

---
## 🐛 Reporting Issues

- Search the issue tracker [first](https://github.com/kbuntrock/openapi-maven-plugin/issues)
- If no existing issue matches, open a new one and include:
  - Steps to reproduce
  - Expected behavior
  - Actual behavior
  - Environment (JDK version, Maven version, OS)

---
## 💡 Feature Requests

We welcome ideas!
When proposing a feature, please include:
- Motivation (what problem it solves)
- Example use cases
- Suggested implementation (if any)

---
## 🧑‍💻 Coding Guidelines

- **Language**: Java 8+ (compatible with JDK 8–21).
- **Style**: Follow standard Java conventions
- **Tests**: Add unit/integration tests for new functionality.
- **Code documentation**: Update Javadoc 
- **General documentation**: Update at documentation located in the ``docs`` sub-folder. (at least the english portion)

---
## ✅ Pull Request Checklist

Before submitting, please ensure:
- [ ] Code compiles with ``mvn clean verify``.
- [ ] Tests pass on JDK 8, 11, 17, 21 (CI will verify).
- [ ] Documentation updated if needed.

---
## 📚 Visualize locally the documentation

Install on your computer **docsify**:
``npm i docsify-cli -g``

And run:
``docsify serve docs``

You can preview the documentation in your browser on http://localhost:3000


---
## 📜 License

By contributing, you agree that your contributions will be licensed under the [MIT licence](https://github.com/kbuntrock/openapi-maven-plugin?tab=MIT-1-ov-file#readme)