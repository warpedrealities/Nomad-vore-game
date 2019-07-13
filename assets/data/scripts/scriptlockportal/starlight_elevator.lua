function hasItem(view,player)
	if (player:getInventory():countItem("mallory's card") > 0 ) then
		view:getSceneController():getGameUniverse():getPlayer():getFlags():setFlag("adaptive_computation",2)
		view:getSceneController():getGameUniverse():getPlayer():addJournal("main","mallory_digested");
		return true;
	end
	return false;

end

function main(view, player)
	flags=player:getFlags()
	var=flags:readFlag("adaptive_computation")
	if var>1 or hasItem(view,player) then
	return true
	end
	return false
end