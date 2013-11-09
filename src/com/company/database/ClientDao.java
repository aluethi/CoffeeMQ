package com.company.database;

import com.company.exception.ClientCreationException;
import com.company.exception.ClientDeletionException;
import com.company.exception.ClientExistsException;
import com.company.model.Client;
import com.company.exception.ClientDoesNotExistException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 07/11/13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class ClientDao {

    protected final Connection con_;

    public ClientDao(Connection con) {
        con_ = con;
    }

    public void createClient(Client c) throws ClientExistsException, ClientCreationException {
        try {
            CallableStatement cst = con_.prepareCall("{ call createClient(?, ?) }");
            cst.setInt(1, c.getId());
            cst.setTimestamp(2, c.getCreated());
            cst.execute();
            cst.close();
        } catch (SQLException e) {
            if(e.getSQLState().equals("23505")) { // DUPLICATE KEY
                throw new ClientExistsException(e);
            }
            throw new ClientCreationException(e);
        }
    }

    public void deleteClient(int id) throws ClientDoesNotExistException, ClientDeletionException {
        try {
            CallableStatement cst = con_.prepareCall("{ call deleteClient(?) }");
            cst.setInt(1, id);
            cst.execute();
            cst.close();
        } catch (SQLException e) {
            if(e.getSQLState().equals("V2001")) { // CUSTOM: ID DOES NOT EXIST
                throw new ClientDoesNotExistException(e);
            }
            throw new ClientDeletionException(e);
        }
    }

}
