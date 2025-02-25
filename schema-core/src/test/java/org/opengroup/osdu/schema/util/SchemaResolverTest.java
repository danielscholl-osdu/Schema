package org.opengroup.osdu.schema.util;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.schema.exceptions.ApplicationException;
import org.opengroup.osdu.schema.exceptions.BadRequestException;
import org.opengroup.osdu.schema.exceptions.NotFoundException;
import org.opengroup.osdu.schema.service.ISchemaService;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class SchemaResolverTest {
    @InjectMocks
    SchemaResolver schemaResolver;

    @Mock
    ISchemaService schemaService;

    @Mock
    JaxRsDpsLog log;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testResolveSchema()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String resolvedSchema = new FileUtils().read("/test_schema/resolvedSchema.json");
        String orginalSchema = new FileUtils().read("/test_schema/originalSchema.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchema.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        JSONAssert.assertEquals(resolvedSchema, schemaResolver.resolveSchema(orginalSchema), JSONCompareMode.LENIENT);
    }

    @Test
    public void testResolveSchema_definitionblock()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String resolvedSchema = new FileUtils().read("/test_schema/resolvedSchema.json");
        String orginalSchema = new FileUtils().read("/test_schema/originalSchemaWithNoDefinitionBlock.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchemaWithDefinitionBlock.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        JSONAssert.assertEquals(resolvedSchema, schemaResolver.resolveSchema(orginalSchema), JSONCompareMode.LENIENT);
    }

    @Test
    public void testResolveSchema_ResolveRefForAttributeInJSONArray()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String resolvedSchema = new FileUtils().read("/test_schema/resolvedSchema_Ref_AttributeInJsonArray.json");
        String orginalSchema = new FileUtils().read("/test_schema/originalSchemaWithRef_AttributeInJsonArray.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchema.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        JSONAssert.assertEquals(resolvedSchema, schemaResolver.resolveSchema(orginalSchema), JSONCompareMode.LENIENT);
    }

    @Test
    public void testResolveSchema_no_definitionblock()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String resolvedSchema = new FileUtils().read("/test_schema/resolvedSchemaWithNoDefinationBlock.json");
        String orginalSchema = new FileUtils().read("/test_schema/originalSchemaWithNoDefinitionBlockNoRef.json");

        JSONAssert.assertEquals(resolvedSchema, schemaResolver.resolveSchema(orginalSchema), JSONCompareMode.LENIENT);
    }

    @Test
    public void testResolveSchema_InvalidExternalPath()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String orginalSchema = new FileUtils().read("/test_schema/originalSchemaWithInvalidExternalPath.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchemaWithDefinitionBlock.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Invalid Request, https://ggl.json not resolvable");
        schemaResolver.resolveSchema(orginalSchema);
    }
    
    @Test
    public void testResolveSchema_RefResolutionUnderDefinitions()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String orginalSchema = new FileUtils().read("/test_schema/originalSchemaWithRefInsideDefinationBlock.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchemaWithDefinitionBlock.json");
        String expectedOutPut = new FileUtils().read("/test_schema/resolvedSchemaWithRefInsideDefinationBlock.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        String resolvedSchema = schemaResolver.resolveSchema(orginalSchema);
        System.out.println(resolvedSchema);
        JSONAssert.assertEquals(new JSONObject(expectedOutPut), new JSONObject(resolvedSchema), JSONCompareMode.STRICT);
    }

    @Test
    public void testResolveSchema_ExternalPathWithInValidSchema()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String orginalSchema = new FileUtils().read("/test_schema/originalSchemaWithInvalidExternalPath3.json");
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Invalid Request, https://www.google.com not a valid Json schema object");
        schemaResolver.resolveSchema(orginalSchema);
    }

    @Test
    public void testResolveSchema_BadRequestExternalPath()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String orginalSchema = new FileUtils().read("/test_schema/originalSchemaWithInvalidExternalPath2.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchemaWithDefinitionBlock.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(
                "Invalid Request, https://schema-service.endpoints.evd-ddl-us-services.cloud.goog/api/sgsd not resolvable");
        schemaResolver.resolveSchema(orginalSchema);
    }

    @Test
    public void testResolveSchema_invalidRefSchema()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenThrow(NotFoundException.class);
        String orginalSchema = new FileUtils().read("/test_schema/originalSchema.json");
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(
                "Invalid input, os:wks:anyCrsFeatureCollection.1.0 not registered but provided as reference");
        schemaResolver.resolveSchema(orginalSchema);
        fail("Should not succeed");

    }

    @Test
    public void testResolveSchema_noDefinitionForRef()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String originalSchema = new FileUtils().read("/test_schema/originalSchemaWithRefButNoDefinition.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchema.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(
                "Invalid input, no 'person' definition found but provided as a reference");
        schemaResolver.resolveSchema(originalSchema);
        fail("Should not succeed");
    }

    @Test
    public void testResolveSchema_withDefinitionForRef()
            throws JSONException, BadRequestException, ApplicationException, NotFoundException, IOException {
        String resolvedSchema = new FileUtils().read("/test_schema/resolvedSchemaWithDefinition.json");
        String originalSchema = new FileUtils().read("/test_schema/originalSchemaRefWithDefinition.json");
        String referenceSchema = new FileUtils().read("/test_schema/referenceSchema.json");
        Mockito.when(schemaService.getSchema("os:wks:anyCrsFeatureCollection.1.0")).thenReturn(referenceSchema);
        JSONAssert.assertEquals(resolvedSchema, schemaResolver.resolveSchema(originalSchema), JSONCompareMode.NON_EXTENSIBLE);
    }
}