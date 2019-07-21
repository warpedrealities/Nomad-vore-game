
function main(tools)  
	if (tools:getGlobalFlags():readFlag("MAIN")>3) then
		return true;
	end
	return false;
end
