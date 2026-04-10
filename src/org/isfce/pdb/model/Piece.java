package org.isfce.pdb.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Piece {
	private Integer id;
	private String nom;
	private String description;
	private BigDecimal etage;		// <- BigDecimal au lieu de (double)
	private TypePiece typePiece;	// < renommé type -> typePiece
	private final Integer installation;

}
