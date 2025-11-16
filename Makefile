.PHONY: help clean test test-unit test-instrumented coverage coverage-unit coverage-instrumented coverage-all build install

# Default target - show help
help:
	@echo "📊 RestoFinder - Available Make Commands"
	@echo "========================================"
	@echo ""
	@echo "🧪 Testing:"
	@echo "  make test              - Run all tests (unit + instrumented)"
	@echo "  make test-unit         - Run unit tests only (~5s)"
	@echo "  make test-instrumented - Run instrumented tests (~30s, requires device)"
	@echo ""
	@echo "📈 Coverage Reports:"
	@echo "  make coverage          - Generate all coverage reports"
	@echo "  make coverage-unit     - Unit test coverage (business logic)"
	@echo "  make coverage-instrumented - Instrumented test coverage (UI components)"
	@echo ""
	@echo "🔧 Build & Deploy:"
	@echo "  make build             - Build debug APK"
	@echo "  make install           - Build and install on device"
	@echo "  make release           - Build release APK"
	@echo ""
	@echo "🧹 Cleanup:"
	@echo "  make clean             - Clean build artifacts"
	@echo "  make clean-test        - Clean test results"
	@echo ""
	@echo "📊 View Reports:"
	@echo "  make view-coverage     - Open unit test coverage report"
	@echo "  make view-coverage-ui  - Open instrumented test coverage report"
	@echo "  make view-tests        - Open test results"
	@echo ""

# Clean build artifacts
clean:
	@echo "🧹 Cleaning build artifacts..."
	./gradlew clean --no-configuration-cache

# Clean test results only
clean-test:
	@echo "🧹 Cleaning test results..."
	rm -rf app/build/reports/tests/
	rm -rf app/build/reports/jacoco/
	rm -rf app/build/test-results/

# Run unit tests
test-unit:
	@echo "🧪 Running unit tests..."
	./gradlew testDebugUnitTest --no-configuration-cache

# Run instrumented tests (requires device/emulator)
test-instrumented:
	@echo "🧪 Running instrumented tests (requires device)..."
	./gradlew connectedDebugAndroidTest --no-configuration-cache

# Run all tests
test: test-unit test-instrumented
	@echo "✅ All tests completed!"

# Generate unit test coverage report
coverage-unit:
	@echo "📊 Generating unit test coverage report..."
	./gradlew testDebugUnitTest --no-configuration-cache
	./gradlew jacocoTestReport --no-configuration-cache
	@echo ""
	@echo "✅ Unit test coverage report generated!"
	@echo "📂 Location: app/build/reports/jacoco/jacocoTestReport/html/index.html"
	@echo "💡 Run 'make view-coverage' to open it"

# Generate instrumented test coverage report
coverage-instrumented:
	@echo "📊 Generating instrumented test coverage report..."
	./gradlew createDebugAndroidTestCoverageReport --no-configuration-cache
	./gradlew jacocoInstrumentedTestReport --no-configuration-cache
	@echo ""
	@echo "✅ Instrumented test coverage report generated!"
	@echo "📂 Location: app/build/reports/jacoco/jacocoInstrumentedTestReport/html/index.html"
	@echo "💡 Run 'make view-coverage-ui' to open it"

# Generate all coverage reports
coverage: coverage-unit
	@echo ""
	@echo "✅ All coverage reports generated!"
	@echo ""
	@echo "📊 Unit Test Coverage (Business Logic):"
	@echo "   app/build/reports/jacoco/jacocoTestReport/html/index.html"
	@echo ""
	@echo "📊 Instrumented Test Coverage (UI Components):"
	@echo "   Note: Run 'make coverage-instrumented' to generate (requires device)"
	@echo ""

# Combined: all tests with coverage
coverage-all: clean-test
	@echo "📊 Running all tests with coverage reports..."
	@make coverage-unit
	@echo ""
	@echo "💡 For UI coverage, run: make coverage-instrumented"

# View coverage report in browser
view-coverage:
	@echo "📂 Opening unit test coverage report..."
	@if [ -f app/build/reports/jacoco/jacocoTestReport/html/index.html ]; then \
		open app/build/reports/jacoco/jacocoTestReport/html/index.html || xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html; \
	else \
		echo "❌ Coverage report not found. Run 'make coverage-unit' first."; \
	fi

# View instrumented test coverage report
view-coverage-ui:
	@echo "📂 Opening instrumented test coverage report..."
	@if [ -f app/build/reports/jacoco/jacocoInstrumentedTestReport/html/index.html ]; then \
		open app/build/reports/jacoco/jacocoInstrumentedTestReport/html/index.html || xdg-open app/build/reports/jacoco/jacocoInstrumentedTestReport/html/index.html; \
	else \
		echo "❌ UI coverage report not found. Run 'make coverage-instrumented' first."; \
	fi

# View test results
view-tests:
	@echo "📂 Opening test results..."
	@if [ -f app/build/reports/tests/testDebugUnitTest/index.html ]; then \
		open app/build/reports/tests/testDebugUnitTest/index.html || xdg-open app/build/reports/tests/testDebugUnitTest/index.html; \
	else \
		echo "❌ Test report not found. Run 'make test-unit' first."; \
	fi

# Build debug APK
build:
	@echo "🔨 Building debug APK..."
	./gradlew assembleDebug --no-configuration-cache
	@echo "✅ APK: app/build/outputs/apk/debug/app-debug.apk"

# Build release APK
release:
	@echo "🔨 Building release APK..."
	./gradlew assembleRelease --no-configuration-cache
	@echo "✅ APK: app/build/outputs/apk/release/app-release.apk"

# Install app on connected device
install:
	@echo "📱 Installing app on device..."
	./gradlew installDebug --no-configuration-cache
	@echo "✅ App installed successfully!"

# Quick test (unit tests only, fast)
quick-test:
	@echo "⚡ Running quick unit tests..."
	./gradlew testDebugUnitTest --no-configuration-cache
	@echo "✅ Quick tests completed!"

# CI/CD - all checks
ci: clean test-unit coverage-unit
	@echo "✅ CI checks completed!"
	@echo "📊 View coverage: make view-coverage"

# Development workflow - quick feedback
dev: test-unit
	@echo "✅ Development checks passed!"
