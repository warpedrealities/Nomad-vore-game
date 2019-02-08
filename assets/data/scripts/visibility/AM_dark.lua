function main(universe)
	variable=universe:getPlayer():getFlags():readFlag("SPACEHULK_SCAN")
	if (variable==1) then
		return true;	
	else
		return false;	
	end

end