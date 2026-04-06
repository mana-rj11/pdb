package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.isfce.pdb.exceptions.CheckException;
import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.exceptions.PKException;

public class FBDAOFactory extends DAOFactory {
	private Connection connexion;
	private ITypePieceDao daoTypePiece = null;

	public FBDAOFactory(Connection connexion) {
		this.connexion = connexion;
	}

	@Override
	public ITypePieceDao getTypePieceDAO() {
		if (daoTypePiece == null)
			daoTypePiece = new SQLTypePieceDao(this);
		return daoTypePiece;
	}

	@Override
	public Connection getConnection() {
		return connexion;
	}

	// recuperer le code d'erreur firebird avec exc.getError...
	// PKException
	// CheckException, violée
	// default = autres erreur SQL
	@Override
	protected void dispatchException(Exception e, String detail) throws InstallationException {
		SQLException exc = (SQLException) e;
		
		throw switch (exc.getErrorCode()) {
		case 335544665 -> new PKException(e.getMessage(), detail);
		case 335544347 -> new CheckException(e.getMessage(), detail);
		default -> new InstallationException("Problème");
		};
	}
	
	private IPieceDao daoPiece = null;
	
	@Override 
	public IPieceDao getPieceDAO() {
		if (daoPiece == null)
			daoPiece = new SQLPieceDao(this);
		return daoPiece;
	}

}
