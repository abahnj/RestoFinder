# GitHub Actions CI/CD Pipeline

Automated testing and quality checks for RestoFinder.

## Workflows

### CI/CD Pipeline (`ci.yml`)
Runs on PRs and pushes to `main` branch.

| Job | Duration | Description |
|-----|----------|-------------|
| Unit Tests & Coverage | ~5-10 min | JaCoCo coverage, 80% threshold (warning only) |
| Lint & Code Quality | ~3-5 min | Android Lint checks |
| Build APK | ~5-8 min | Debug APK (requires tests + lint) |
| Instrumented Tests | ~20-30 min | Android 13 emulator (main branch only) |
| Test Summary | ~1-2 min | Publishes unified test results |

**Features:**
- Coverage reports with PR comments
- Parallel job execution
- Gradle & AVD caching
- Test result publishing

### Release Build (`release.yml`)
Builds and publishes release APK when version tags (`v*`) are pushed.
Uses debug keystore for signing (assignment purposes).

### Dependabot (`dependabot.yml`)
Weekly dependency updates for Gradle and GitHub Actions.

## Local Testing

```bash
# Run all CI checks
make ci

# Individual checks
make coverage-unit       # Unit tests with coverage
./gradlew lintDebug     # Lint checks
make build              # Build APK
make test-instrumented  # UI tests (requires emulator)

# View reports
make view-coverage
make view-tests
```

## Configuration

**Coverage Threshold:** 80% (non-blocking warning)
**Artifacts Retention:** 30 days (dev), 90 days (release)
**Release Signing:** Debug keystore (no secrets required)
