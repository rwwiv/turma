{states: A,Q0,Q2,R}
{start: Q0}
{accept: A}
{reject: R}
{alpha: b,c}
{tape-alpha: b,c,X}

rwRt Q0 b X Q2;
rwRt Q0 c X Q2;
rLl Q2 b;
rLl Q2 c;
