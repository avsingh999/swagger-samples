/**
 * Copyright 2016 SmartBear Software
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.swagger.sample.resource;

import io.swagger.oas.annotations.Operation;
import io.swagger.oas.annotations.Parameter;
import io.swagger.oas.annotations.media.Content;
import io.swagger.oas.annotations.media.Schema;
import io.swagger.oas.annotations.responses.ApiResponse;
import io.swagger.sample.data.PetData;
import io.swagger.sample.model.Pet;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/pet")
@Produces({"application/json", "application/xml"})
public class PetResource {
    static PetData petData = new PetData();

    @GET
    @Path("/{petId}")
    @Operation(summary = "Find pet by ID",
            description = "Returns a pet when 0 < ID <= 10.  ID > 10 or nonintegers will simulate API error conditions",
            responses = {
                    @ApiResponse(description = "The pet", content = @Content(
                            schema = @Schema(implementation = Pet.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
                    @ApiResponse(responseCode = "404", description = "Pet not found")
            })
    public Response getPetById(
            @Parameter(description = "ID of pet that needs to be fetched"/*, _enum = "range[1,10]"*/, required = true)
            @PathParam("petId") Long petId) throws io.swagger.sample.exception.NotFoundException {
        Pet pet = petData.getPetById(petId);
        if (null != pet) {
            return Response.ok().entity(pet).build();
        } else {
            throw new io.swagger.sample.exception.NotFoundException(404, "Pet not found");
        }
    }

    @POST
    @Operation(summary = "Add a new pet to the store",
            responses = {
                    @ApiResponse(responseCode = "405", description = "Invalid input")
            })
    public Response addPet(
            @Parameter(description = "Pet object that needs to be added to the store", required = true) Pet pet) {
        petData.addPet(pet);
        return Response.ok().entity("SUCCESS").build();
    }

    @PUT
    @Operation(summary = "Update an existing pet",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
                    @ApiResponse(responseCode = "404", description = "Pet not found"),
                    @ApiResponse(responseCode = "405", description = "Validation exception")})
    public Response updatePet(
            @Parameter(description = "Pet object that needs to be added to the store", required = true) Pet pet) {
        petData.addPet(pet);
        return Response.ok().entity("SUCCESS").build();
    }

    @GET
    @Path("/findByStatus")
    @Operation(summary = "Finds Pets by status",
            description = "Multiple status values can be provided with comma seperated strings",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Pet.class))),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid status value"
                    )}
    )
    public Response findPetsByStatus(
            @Parameter(description = "Status values that need to be considered for filter", required = true/*, defaultValue = "available", allowableValues = "available,pending,sold", allowMultiple = true*/) @QueryParam("status") String status,
            @BeanParam QueryResultBean qr
    ) {
        return Response.ok(petData.findPetByStatus(status)).build();
    }

    @GET
    @Path("/findByTags")
    @Operation(summary = "Finds Pets by tags",
            description = "Muliple tags can be provided with comma seperated strings. Use tag1, tag2, tag3 for testing.",
            responses = {
                    @ApiResponse(description = "Pets matching criteria",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Pet.class))
                    ),
                    @ApiResponse(description = "Invalid tag value", responseCode = "400")
            })
    @Deprecated
    public Response findPetsByTags(
            @Parameter(description = "Tags to filter by", required = true/*, allowMultiple = true*/) @QueryParam("tags") String tags) {
        return Response.ok(petData.findPetByTags(tags)).build();
    }
}
