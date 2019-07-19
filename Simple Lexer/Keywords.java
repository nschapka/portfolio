package roboCompile.ABB.DataTypes;

public enum Keywords {
	//flow control
	IF,
	THEN,
	ENDIF, 
	FOR,
	FROM,
	TO,
	STEP,
	ENDFOR, 
	WHILE, 
	DO,
	ENDWHILE,
	//motions
	MOVEJ,
	MOVEL,
	MOVEABSJ,
	MOVEC,
	OFFS,
	RELTOOL,
	//general data types
	NUM, 
	INT,
	STRING,
	CHAR,
	//position data types
	POS, 
	POSE, 
	ROBTARGET, 
	JOINTTARGET,
	//scope
	VAR,
	PERS,
	CONST,
	PROC,
	FUNC,
	MODULE,
	ENDPROC,
	ENDFUNC,
	ENDMODULE,
	//other
	;
}
