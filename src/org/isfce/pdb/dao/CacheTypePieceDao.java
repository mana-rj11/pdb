package org.isfce.pdb.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.isfce.pdb.model.TypePiece;

public class CacheTypePieceDao implements ITypePieceDao {
	private ITypePieceDao dao;
	private Map<String, TypePiece> cache = new HashMap<>();
	
	public CacheTypePieceDao(ITypePieceDao dao) {
		this.dao = dao;
	}

	@Override
	public Optional<TypePiece> getFromID(String id) {
		// il existe dan sla cache ?
		if (cache.containsKey(id))
			return Optional.of(cache.get(id));
		// va chercher l'objet en BD
		var oObj = dao.getFromID(id);
		// rajoute l'objet au cache s'il existe
		oObj.ifPresent((obj) -> cache.put(id, obj));
		return oObj;
	}

	@Override
	public List<TypePiece> getListe(String regExpr) {
		var liste = dao.getListe(null);
		// rajoute tous les objets dans la cache
		liste.forEach((e) -> cache.put(e.getCode(), e));
		return liste;
	}

	@Override
	public TypePiece insert(TypePiece objet) throws Exception {
		objet = dao.insert(objet);
		// ajoute au cache après insert
		cache.put(objet.getCode(), objet);
		return objet;
	}
}
