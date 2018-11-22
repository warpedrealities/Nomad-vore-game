
function main(tools)  
	a=math.random(0,8)
	if (tools:getGlobalFlags():readFlag("piratebounty0")==1) then
		tools:getGlobalFlags():setFlag("piratebounty0",2)
		--spawn pirate
		tools:spawn("sloopB","dreadpirate_roberta")
	end  
end
