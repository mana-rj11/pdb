package org.isfce.pdb.dao;

// import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDaoPiece {
	
	private static IPieceDao dao;
	
	// TypePiece attendu pour les tests
	static TypePiece typeSalon = new TypePiece("SALON", "Salon", false);
	
	// Piece attendue pour nos tests - builder utilisé et correspondre aux nouvelles données du script
	static TypePiece tp = new TypePiece("SALON", "Salon", false);
	static Piece p = Piece.builder()
			.id(1)
			.nom("SALON")
			.description("Salon principal")
			.etage(new BigDecimal("0.0"))
			.typePiece(tp)
			.installation(3)
			.build();
	
	// nouvelle pièce à insérer (sans ID!)
	static Piece pn = Piece.builder()
			.nom("SALON2")
			.description("Salon principal2")
			.etage(new BigDecimal(1.0).setScale(1))
			.typePiece(tp)
			.installation(3)
			.build();
	
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
		assertEquals(p, oObj.get()); 	// <- compare Piece avec Piece (p au lieu de tp "TypePiece")
	}
	
	// Test. Cours du 08 avril 
	// @Test 
	// void testGetListe() throws SQLException {
	//	var liste = dao.getListe(null);
	//	assertTrue(liste.size() > 0);
	//	assertEquals(tp, liste.get(0));
	// }
	
	@Test 
	void testInsert() throws Exception {
		Piece pn2 = dao.insert(pn);				// insert -> firebird génère l'ID
		assertNotNull(pn.getId());				// l'ID a été injecté dans pn
		var oObj = dao.getFromID(pn.getId());	// relit depuis la BD
		assertTrue(oObj.isPresent());			
		assertEquals(pn2, oObj.get());			// compare inséré vs relu
		// pn2 a maintenant un ID -> AssertionError si on réinsère
		assertThrows(AssertionError.class, () -> dao.insert(pn2));
	}
	
}
