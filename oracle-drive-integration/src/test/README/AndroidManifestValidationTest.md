This test suite validates AndroidManifest.xml structure using pure XML parsing.
Assumed test framework: JUnit 5 (Jupiter). If your project uses JUnit 4, rename AndroidManifestValidationTest_JUnit4.kt.disabled to AndroidManifestValidationTest.kt and remove the JUnit 5 file.

Key scenarios covered:
- Well-formed baseline manifest
- Namespace presence/consistency
- Malformed XML failures
- DOCTYPE/XXE prevention via parser configuration
- Detection of unexpected attributes for policy checks