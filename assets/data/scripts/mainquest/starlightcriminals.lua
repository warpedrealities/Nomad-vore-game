function main(factionlibrary, player)
flags=player:getFlags()
var0=flags:readFlag("adaptive_computation")
	if var0==1 then
	return true
	end
	return false
end