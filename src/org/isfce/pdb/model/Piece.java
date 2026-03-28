package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Piece {

	
	private final int num;
	private final String nom;
	private final String description;
	private final double etage;
	private final TypePiece type;	// objet TypePiece complet!
	private final int installation;

}
