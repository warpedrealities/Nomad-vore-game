function main(factionlibrary, player)
	flags=player:getFlags()
var0=flags:readFlag("rockscleared")
var1=flags:readFlag("alphaminoris2_victory")
	if var0==1 and var1==0 then
	return true
	end
	return false
end