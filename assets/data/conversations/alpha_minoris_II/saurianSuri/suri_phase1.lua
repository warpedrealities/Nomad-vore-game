function main(factionlibrary, player)
	--check for absence of scales
	scales=player:getLook():getPart("scales");
	if (scales==null) then
		return false;
	end
	coverage=scales;
	if (coverage:getValue("covering")==0) then
		return true;
	end 
	
	return false;
end