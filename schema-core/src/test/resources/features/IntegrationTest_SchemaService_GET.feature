Feature: To verify functionality of GET schema Service

  #Commons test steps are accomplished here
  Background: Common steps for all tests are executed
    Given I generate user token and set request headers
    Given I hit schema service GET List API with "authority" , "SchemaSanityTest" , "true"
    Given I hit schema service POST API with "/input_payloads/postInPrivateScope_positiveScenario.json" and data-partition-id as "opendes" only if status is not development

  @SchemaServicewip
  Scenario Outline: Verify that Schema Service's GET API returns schema from shared tenant if there is a conflicting schema present in private scope
    Given I hit schema service GET API with <SchemaId>
    #when the schema being searched for is present both in shared and private scope
    Then service should respond back with <ReponseStatusCode> and <ResponseMessage>

    Examples: 
      | SchemaId                                                 | ReponseStatusCode | ResponseMessage                                        |
      | "shared:schema:preferred_over_private.1.0.0"             | "200"             | "/input_payloads/schemaPresentInSharedTenant.json"     |
      | "duplicateStorageSchema:storgaeSchema1:entity1:10.1.100" | "200"             | "/input_payloads/getList_Storage_duplicate_set_1.json" |

  @SchemaService_BugToBeResolved
  Scenario Outline: Verify that Schema Service's GET API throws correct exception when auth token is invalid
    Given I hit schema service GET API with <SchemaId> and auth token invalid
    Then service should respond back with error <ReponseStatusCode> and <ResponseMessage>

    Examples: 
      | SchemaId                                           | ReponseStatusCode | ResponseMessage                                             |
      | "SchemaSanityTest:testsource:testentity.431.225.0" | "401"             | "/output_payloads/SchemaPost_InvalidExpiredTokenError.json" |

  @SchemaService
  Scenario Outline: Verify that Schema Service's GET API throws correct exception when requested schemaId does not exist or is Malformed
    Given I hit schema service GET API with <SchemaId>
    Then service should respond back with error <ReponseStatusCode> and <ResponseMessage>

    Examples: 
      | SchemaId                                    | ReponseStatusCode | ResponseMessage                                 |
      | "NonExisting_slb:techlog:wellbore.5.1024.0" | "404"             | "/output_payloads/SchemaGet_NotFoundError.json" |
      | "testsource:common:testentity.1.0"          | "404"             | "/output_payloads/SchemaGet_NotFoundError.json" |
      | "common..testsource:testentity.1.0"         | "404"             | "/output_payloads/SchemaGet_NotFoundError.json" |
      | "common..testsource..testentity"            | "404"             | "/output_payloads/SchemaGet_NotFoundError.json" |
      | "common.testsource..testentity1.0"          | "404"             | "/output_payloads/SchemaGet_NotFoundError.json" |

  @SchemaService
  Scenario Outline: Verify that Schema Service's GET list API throws correct exception if mandatory headers are blank in request header
    #Given I hit schema service GET List API with <parameter> and <value> having blank <header>
    Given I hit schema service GET API with <SchemaId> having blank <header>
    Then service should respond back with error <ReponseStatusCode> and <ResponseMessage>

    Examples: 
      | SchemaId                                           | ReponseStatusCode | ResponseMessage                                          | header              |
      | "SchemaSanityTest:testsource:testentity.431.225.0" | "401"             | "/output_payloads/SchemaGet_MissingAuthorization.json"   | "authorization"     |
      | "SchemaSanityTest:testsource:testentity.431.225.0" | "401"             | "/output_payloads/SchemaGet_MissingDataPartitionId.json" | "data-partition-id" |

  @BugFixRequired ##Bug 482049: [OSDU Schema Service] - GET List not working as expected
  Scenario Outline: Verify that Schema Service's GET list API returns list of schema all scopes
    Given I hit schema service GET List API with <parameter> and <value>
    Then service should respond back with schemaInfo list matching <parameter> and <value>

    Examples: 
      | parameter            | value              |
      | "authority"          | "SchemaSanityTest" |
      | "source"             | "testSource"       |
      | "entityType"         | "testEntity"       |
      | "schemaVersionMajor" | "306"              |
      | "schemaVersionMinor" | "100"              |
      | "status"             | "PUBLISHED"        |
      | "scope"              | "INTERNAL"         |

  @BugFixRequired ##Bug 482049: [OSDU Schema Service] - GET List not working as expected
  Scenario Outline: Verify that Schema Service's GET list API returns correct list of schemas when queried with multiple parameters
    Given I hit schema service GET List API with query parameters having values <authority>, <source>, <entityType>, <status>, <scope>, <schemaVersionMajor>, <schemaVersionMinor>, <schemaVersionPatch>, <count>
    Then service should respond back with <responseCode> and schemaInfo list values matching to input

    Examples: 
      | authority          | source       | entityType   | status      | scope      | schemaVersionMajor | schemaVersionMinor | schemaVersionPatch | count | responseCode |
      | "SchemaSanityTest" | "NA"         | "NA"         | "NA"        | "NA"       | "306"              | "100"              | "NA"               | "NA"  | "200"        |
      | "SchemaSanityTest" | "testSource" | "NA"         | "NA"        | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "SchemaSanityTest" | "testSource" | "testEntity" | "NA"        | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "SchemaSanityTest" | "testSource" | "testEntity" | "PUBLISHED" | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "SchemaSanityTest" | "testSource" | "testEntity" | "PUBLISHED" | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "NA"         | "NA"        | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "NA"         | "PUBLISHED" | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "testEntity" | "PUBLISHED" | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "testSource" | "testEntity" | "PUBLISHED" | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "SchemaSanityTest" | "NA"         | "NA"         | "NA"        | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "SchemaSanityTest" | "NA"         | "NA"         | "PUBLISHED" | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "SchemaSanityTest" | "NA"         | "testEntity" | "NA"        | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "testSource" | "NA"         | "NA"        | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "testSource" | "testEntity" | "NA"        | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "testSource" | "NA"         | "PUBLISHED" | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "testSource" | "NA"         | "NA"        | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "testEntity" | "NA"        | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "testEntity" | "PUBLISHED" | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "testEntity" | "NA"        | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "NA"         | "PUBLISHED" | "NA"       | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |
      | "NA"               | "NA"         | "NA"         | "PUBLISHED" | "INTERNAL" | "NA"               | "NA"               | "NA"               | "NA"  | "200"        |

  @SchemaService
  Scenario Outline: Verify that Schema Service's GET list API handles patch filter error combinations elegantly with correct error messages
    Given I hit schema service GET List API with filters of <authority>, <schemaVersionMajor>, <schemaVersionMinor>, <schemaVersionPatch> and getLatest flag is <latestFlag>
    Then service should respond back with error <ReponseStatusCode> and <ResponseMessage>

    Examples: 
      | authority          | schemaVersionMajor | schemaVersionMinor | schemaVersionPatch | latestFlag | ReponseStatusCode | ResponseMessage                                                          |
      | "SchemaSanityTest" | "NA"               | "latestVersion"    | "NA"               | "True"     | "400"             | "/output_payloads/GetSchema_incorrectPatchFilter_MinorWithoutMajor.json" |
      | "SchemaSanityTest" | "NA"               | "NA"               | "latestVersion"    | "True"     | "400"             | "/output_payloads/GetSchema_incorrectPatchFilter_PatchwithoutMinor.json" |
      | "SchemaSanityTest" | "NA"               | "latestVersion"    | "latestVersion"    | "True"     | "400"             | "/output_payloads/GetSchema_incorrectPatchFilter_MinorWithoutMajor.json" |
      | "SchemaSanityTest" | "latestVersion"    | "NA"               | "latestVersion"    | "True"     | "400"             | "/output_payloads/GetSchema_incorrectPatchFilter_PatchwithoutMinor.json" |

  @SchemaService
  Scenario Outline: Verify that Schema Service's GET list API handles patch filter combinations elegantly with correct success messages
    Given I hit schema service GET List API with filters of <authority>, <schemaVersionMajor>, <schemaVersionMinor>, <schemaVersionPatch> and getLatest flag is <latestFlag>
    Then service should respond back with <ReponseStatusCode> and <ResponseMessage> schema with correct major, minor and patch version.

    Examples: 
      | authority          | schemaVersionMajor | schemaVersionMinor | schemaVersionPatch | latestFlag | ReponseStatusCode | ResponseMessage                                                          |
      | "SchemaSanityTest" | "latestVersion"    | "latestVersion"    | "latestVersion"    | "False"    | "200"             | "/output_payloads/GetSchema_incorrectPatchFilter_MinorWithoutMajor.json" |
      | "SchemaSanityTest" | "NA"               | "latestVersion"    | "latestVersion"    | "False"    | "200"             | "/output_payloads/GetSchema_incorrectPatchFilter_MinorWithoutMajor.json" |
      | "SchemaSanityTest" | "latestVersion"    | "NA"               | "latestVersion"    | "False"    | "200"             | "/output_payloads/GetSchema_incorrectPatchFilter_MinorWithoutMajor.json" |
      | "SchemaSanityTest" | "latestVersion"    | "latestVersion"    | "NA"               | "True"     | "200"             | "/output_payloads/GetSchema_incorrectPatchFilter_MinorWithoutMajor.json" |
      | "SchemaSanityTest" | "latestVersion"    | "latestVersion"    | "latestVersion"    | "True"     | "200"             | "/output_payloads/GetSchema_incorrectPatchFilter_MinorWithoutMajor.json" |