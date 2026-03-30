package org.isfce.pdb.dao;

// import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.isfce.pdb.databases.connexion.ConnexionFromFile;
import org.isfce.pdb.databases.connexion.ConnexionSingleton;
import org.isfce.pdb.databases.uri.Databases;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.TypePiece;
import org.isfce.pdb.util.DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestDaoPiece {
	
	private static IPieceDao dao;
	
	// TypePiece attendu pour les tests
	static TypePiece typeSalon = new TypePiece("SALON", "Salon", false);
	
	// Piece attendue pour nos tests
	static Piece pieceAttendue = new Piece(1, "SALON_MAB", "Mon Salon", 1.0, typeSalon, 1);
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(
			new ConnexionFromFile("./ressources/connexionPDB2526_test.properties",
								  Databases.FIREBIRD));
		// Reinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), 
									  "./ressources/scriptInitDBTest.sql");
		var factory = DAOFactory.getDAOFactory(
			DAOFactory.TypePersistance.FIREBIRD,
			ConnexionSingleton.getConnexion());
		dao = factory.getPieceDAO();
	}
	
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// reinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(),
									  "./ressources/scriptInitDBTest.sql");
		ConnexionSingleton.liberationConnexion();
	}
	
	@Test
	void testGetFromId() throws SQLException {
		var oObj = dao.getFromID(1);
		assertTrue(oObj.isPresent());
		assertEquals(pieceAttendue, oObj.get());
	}
	
	@Test 
	void testGetListe() throws SQLException {
		var liste = dao.getListe(null);
		assertTrue(liste.size() > 0);
		assertEquals(pieceAttendue, liste.get(0));
	}
	
}
