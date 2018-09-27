function main(factionlibrary, player)
	flags=player:getFlags()
var=flags:readFlag("ratquest")
	if var>0 and var<4 then
	return true
	end
	return false
end