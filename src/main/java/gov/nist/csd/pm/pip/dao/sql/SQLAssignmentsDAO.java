package gov.nist.csd.pm.pip.dao.sql;

import gov.nist.csd.pm.model.exceptions.DatabaseException;
import gov.nist.csd.pm.pip.dao.AssignmentsDAO;
import gov.nist.csd.pm.model.graph.Node;
import gov.nist.csd.pm.pip.model.DatabaseContext;

import java.sql.*;

public class SQLAssignmentsDAO implements AssignmentsDAO {

    private SQLConnection mysql;

    public SQLAssignmentsDAO(DatabaseContext ctx) throws DatabaseException {
        mysql = new SQLConnection(ctx.getHost(), ctx.getPort(), ctx.getUsername(), ctx.getPassword(), ctx.getSchema());
    }

    @Override
    public void createAssignment(Node child, Node parent) throws DatabaseException {
        try {
            CallableStatement stmt = mysql.getConnection().prepareCall("{call create_assignment(?,?,?)}");
            stmt.setInt(1, (int) parent.getID());
            stmt.setInt(2, (int) child.getID());
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();
            String errorMsg = stmt.getString(3);
            if (errorMsg!= null && errorMsg.length() > 0) {
                throw new DatabaseException(2000, errorMsg);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void deleteAssignment(long childId, long parentId) throws DatabaseException {
        try {
            CallableStatement stmt = mysql.getConnection().prepareCall("{call delete_assignment(?,?,?)}");

            stmt.setLong(1, parentId);
            stmt.setLong(2, childId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();
            String errorMsg = stmt.getString(3);
            if (errorMsg!= null && errorMsg.length() > 0) {
                throw new DatabaseException(2000, errorMsg);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }


}
