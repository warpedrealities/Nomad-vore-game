function main(factionlibrary, player)
	--check for absence of scales
	scales=player:getLook():getPart("scales");
	if (scales==null) then
		return false;
	end
	tail=player:getLook():getPart("tail");
	if not (tail==null) then
		return false;
	end
	coverage=scales;
	if (coverage:getValue("covering")==2) then
		return true;
	end 
	
	return false;
end