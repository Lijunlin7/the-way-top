function x = Bisection(a, b)
    eps = 1e-12;
    if f(a) * f(b)<0
        while 1
            x = (b+a)/2;
            if f(x) * f(a) < 0
                b = x;
            elseif f(x) * f(b) < 0
                a = x;
            elseif abs(f(x))<eps || b-a < eps
                break;
            end
        end
    else
        error('Fail to use bisection algorithm!');
    end
           
end

function y = f(x)
   y =31*x^2-360*x+191;
end