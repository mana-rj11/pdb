package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.TypePiece;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class SQLPieceDao implements IPieceDao {
	
	private static String SQL_GET_FROM_ID = """
			SELECT p.NUM_PIE, p.NOM_PIE, p.DESCRIPTION_PIE,
				p.ETAGE_PIE, p.FKTYPE_PIE, p.FKINSTALLATION_PIE
			FROM TPIECE p
			WHERE p.NUM_PIE = ?
			""";
	
	private static String SQL_GET_LISTE = """
			SELECT p.NUM_PIE, p.NOM_PIE, p.DESCRIPTION_PIE,
				p.ETAGE_PIE, p.FKTYPE_PIE, p.FKINSTALLATION_PIE
			FROM TPIECE p
			ORDER BY p.NUM_PIE
			""";
	
	private Connection connexion;
	private DAOFactory factory;
	
	public SQLPieceDao(DAOFactory factory) {
		this.factory = factory;
		this.connexion = factory.getConnection();
	}
	
	// Plus utulisée avec le nouveau code 
	
	/**
	 * Construire un Objet Piece à partir du ResultSet courant
	 */
	private Piece buildPiece(ResultSet rs) throws SQLException {
		// ici on récupère le TypePiece via son DAO
		ITypePieceDao daoTypePiece = factory.getTypePieceDAO();
		var typePiece = daoTypePiece.getFromID(rs.getString("FKTYPE_PIE"));
		
		if (typePiece.isPresent()) {
			return Piece.builder()
				.id(rs.getInt("NUM_PIE"))
				.nom(rs.getString("NOM_PIE"))
				.description(rs.getString("DESCRIPTION_PIE"))
				.etage(rs.getBigDecimal("ETAGE_PIE"))
				.typePiece(typePiece.get())
				.installation(rs.getInt("FKINSTALLATION_PIE"))
				.build();
			
		}
		return null;
	}
	
	
	@Override
	public Optional<Piece> getFromID(Integer id) {
		Piece obj = null;
		try (PreparedStatement ps = connexion.prepareStatement(SQL_GET_FROM_ID)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String typeP = rs.getString("FKTYPE_PIE");
				TypePiece tp = factory.getTypePieceDAO().getFromID(typeP).get();
				obj = Piece.builder()
						.id(id)
						.nom(rs.getString("NOM_PIE"))
						.description(rs.getString("DESCRIPTION_PIE"))
						.etage(rs.getBigDecimal("ETAGE_PIE"))
						.typePiece(tp)
						.installation(rs.getInt("FKINSTALLATION_PIE"))
						.build();		
				log.debug("Object Piece is created : " + obj);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return Optional.ofNullable(obj);
	}
	
	@Override 
	public List<Piece> getListe(String regExpr) {
		List<Piece> liste = new ArrayList<>(20);
		try (Statement st = connexion.createStatement()) {
			ResultSet rs = st.executeQuery(SQL_GET_LISTE);
			while (rs.next()) {
				Piece p = buildPiece(rs);
				if (p != null)
					liste.add(p);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return liste;
	}
	
	}
