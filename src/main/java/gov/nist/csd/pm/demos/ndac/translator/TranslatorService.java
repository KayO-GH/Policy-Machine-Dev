package gov.nist.csd.pm.demos.ndac.translator;

import gov.nist.csd.pm.model.exceptions.InvalidEntityException;
import gov.nist.csd.pm.model.exceptions.*;
import gov.nist.csd.pm.demos.ndac.translator.algorithms.*;
import gov.nist.csd.pm.demos.ndac.translator.exceptions.PolicyMachineException;
import gov.nist.csd.pm.pep.response.TranslateResponse;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class TranslatorService {
    private PmManager  pmManager;
    private DbManager  dbManager;
    public TranslatorService() {
    }

    public TranslateResponse translate(String sql, String username, long process, String host, int port,
                                       String dbUsername, String dbPassword, String database)
            throws SQLException, IOException, ClassNotFoundException,
            JSQLParserException, PolicyMachineException, PmException, InvalidEntityException {
        Algorithm algorithm = null;
        pmManager = new PmManager(username, process);

        //create an ID for this algorithm
        String id = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
        System.out.println("Translating sql with id = " + id);
        pmManager.addActiveSql(id);

        dbManager = new DbManager();
        dbManager.setConnection(host, port, dbUsername, dbPassword);
        dbManager.setDatabase(database);

        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Select) {
            algorithm = new SelectAlgorithm(id, (Select) statement, pmManager, dbManager);
        } else if (statement instanceof Insert) {
            algorithm = new InsertAlgorithm(id, (Insert) statement, pmManager, dbManager);
        } else if (statement instanceof Update) {
            algorithm = new UpdateAlgorithm(id, (Update) statement, pmManager, dbManager);
        } else if (statement instanceof Delete) {
        }

        if(algorithm != null) {
            return new TranslateResponse(id, algorithm.run());
        } else {
            throw new PmException(6000, "Algorithm returned null");
        }
    }
}
