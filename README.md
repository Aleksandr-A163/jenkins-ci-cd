# Jenkins CI/CD jobs uploader for mobile-appium (Gradle)

This repository is adapted from the Otus CI/CD jobs uploader pattern for a Gradle-based mobile project.

## What it creates
- `jobs_uploader` — uploads Jenkins jobs via Jenkins Job Builder
- `mobile_tests` — runs the pipeline from `Aleksandr-A163/mobile-appium`
- `running_tests` — helper job to trigger `mobile_tests` and publish a merged Allure report
