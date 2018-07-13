package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.ConnectRequest;
import gov.nist.policyserver.requests.DataRequest;
import gov.nist.policyserver.requests.SetIntervalRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.ConfigurationService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import static gov.nist.policyserver.common.Constants.SUCCESS;

@Path("/configuration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationResource {
    private ConfigurationService configService = new ConfigurationService();

    @GET
    public Response getConfiguration() throws ClassNotFoundException, SQLException, IOException, DatabaseException, InvalidPropertyException {
        return new ApiResponse(configService.save()).toResponse();
    }

    @Path("connection")
    @POST
    public Response connect(ConnectRequest request) throws DatabaseException, ConfigurationException, SQLException, IOException, ClassNotFoundException, InvalidPropertyException {
        String database = request.getDatabase();
        String host = request.getHost();
        int port = request.getPort();
        String schema = request.getSchema();
        String username = request.getUsername();
        String password = request.getPassword();

        configService.connect(database, host, port, schema, username, password);

        return new ApiResponse(SUCCESS).toResponse();
    }

    @Path("interval")
    @POST
    public Response setInterval(SetIntervalRequest request) throws ConfigurationException {
        int interval = request.getInterval();

        configService.setInterval(interval);
        return new ApiResponse(interval).toResponse();
    }

    @Path("data")
    @POST
    public Response importData(@QueryParam("session") String session,
                               @QueryParam("process") long process,
                               ConnectRequest request) throws DatabaseException, ConfigurationException, InvalidNodeTypeException, InvalidPropertyException, AssignmentExistsException, NodeNotFoundException, NameInNamespaceNotFoundException, InvalidAssignmentException, SQLException, IOException, ClassNotFoundException, UnexpectedNumberOfNodesException, AssociationExistsException {
        String host = request.getHost();
        int port = request.getPort();
        String schema = request.getSchema();
        String username = request.getUsername();
        String password = request.getPassword();

        configService.importData(host, port, schema, username, password);

        return new ApiResponse(SUCCESS).toResponse();
    }

    @Path("data/tables")
    @POST
    public Response getData(DataRequest request,
                            @QueryParam("session") String session,
                            @QueryParam("process") long process) throws PmException, IOException {
        String host = request.getHost();
        int port = request.getPort();
        String username = request.getUsername();
        String password = request.getPassword();
        String schema = request.getSchema();
        String tableName = request.getTable();

        return new ApiResponse(configService.getData(host, port, username, password, schema, tableName)).toResponse();
    }

    @Path("data/files")
    @POST
    public Response uploadFiles(String[] files,
                                @QueryParam("session") String session,
                                @QueryParam("process") long process) throws InvalidPropertyException, AssignmentExistsException, DatabaseException, InvalidKeySpecException, NodeNotFoundException, NodeIdExistsException, NodeNameExistsException, NodeNameExistsInNamespaceException, NoSuchAlgorithmException, NullNameException, ConfigurationException, NullTypeException, InvalidNodeTypeException, InvalidAssignmentException, IOException, ClassNotFoundException, SQLException, UnexpectedNumberOfNodesException, AssociationExistsException, NoBaseIdException {
        configService.uploadFiles(files);
        return new ApiResponse(SUCCESS).toResponse();
    }


    @Path("graph")
    @GET
    public Response getGraph() throws NodeNotFoundException, InvalidNodeTypeException, InvalidPropertyException, ClassNotFoundException, SQLException, DatabaseException, IOException {
        return new ApiResponse(configService.getJsonGraph()).toResponse();
    }

    @Path("graph/users")
    @GET
    public Response getUserGraph() throws NodeNotFoundException, InvalidNodeTypeException, InvalidPropertyException, ClassNotFoundException, SQLException, DatabaseException, IOException {
        System.out.println("in user");
        return new ApiResponse(configService.getUserGraph()).toResponse();
    }

    @Path("graph/objects")
    @GET
    public Response getObjGraph() throws NodeNotFoundException, InvalidNodeTypeException, InvalidPropertyException, ClassNotFoundException, SQLException, DatabaseException, IOException {
        return new ApiResponse(configService.getObjGraph()).toResponse();
    }
}
