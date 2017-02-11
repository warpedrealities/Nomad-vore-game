function main(factionlibrary, player)
	--check for absence of scales
	scales=player:getLook():getPart("scales");
	if (scales==null) then
		return true;
	end
	
	return false;
end