package remembrall;

public enum Atom {
	typeInt,
	typeDouble,
	typeString,
	typeBool,
	typeTime, 
	typeDatetime,
	typeLocation,
	typeWeather,
	typeNetInfo,
	trueKw,
	falseKw,
	repeatKw,
	returnKw,
	repeatUntilKw,
	ifKw,
	elseKw,
	andKw,
	orKw,
	whenKw,
	doKw,
	everyKw,
	startKw,		
	inclKw,
	nullKw,
	nonImportantKw,
	
	unrecognized,
	
	identifier,
	intConst,
	doubleConst,
	stringConst,
	
	doublePlus, //++
	plusBecomes, // +=
	plusOp, //+
	doubleMinus, //--
	minusBecomes, // -=
	minusOp, //-
	equalsOp, //==
	becomesOp, //=
	notEqual, //!=
	notOp, //!
	lessEquals, //<=
	lessThan, //<
	moreEquals, //>=
	moreThan, //>
	
	multOp, //*
	divOp, // /
	dotOp, // .
	commaOp, //,
	lBracket, // [
	rBracket, // ]
	lParent, // (
	rParent //)

}
