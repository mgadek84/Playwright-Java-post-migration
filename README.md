# Playwright Java suite

JUnit 5 + Playwright Java port of the Robot Framework suites in `../tests/RF`. Page objects live in `pages/`, the reqres.in client in `api/`, and tests in `tests/api` and `tests/ui`.

Requires **JDK 17+** and **Google Chrome**. The Maven Wrapper (`mvnw` / `mvnw.cmd`) is included.

## Run the migrated tests

Run these from the repository root.

On PowerShell, quote every `-D...` argument. An unquoted comma (`-Dtest=A,B`) is parsed as an array and fails with "Missing argument in parameter list."

```powershell
# Windows (PowerShell) — runs the two migrated tests
.\mvnw.cmd test "-DHEADLESS=true"
```

```powershell
# Optional: select classes explicitly (quotes are required)
.\mvnw.cmd test "-DHEADLESS=true" "-Dtest=ReqresAuthTest,SauceDemoInventoryTest"
```

```bash
# macOS / Linux
./mvnw test -DHEADLESS=true
```

Leave off `-DHEADLESS=true` to see the browser. `UI_BROWSER` defaults to `chrome`. Use `chromium`, `firefox`, or `webkit` for Playwright's bundled browsers (downloaded on first use).

Allure results go to `target/allure-results`. After a run:

```bash
./mvnw allure:serve
```
