
function main(controllable, view, script)  
	view:createNPC("Alpha_Minoris_IIA/synthoid",controllable:getPosition(),false)
	controllable:Remove(false)
end  