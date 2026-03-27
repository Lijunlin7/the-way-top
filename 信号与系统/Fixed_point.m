function x = Fixed_point(x0)
    eps = 1e-12;
    x1 = x0;
    while 1
        x2 = phi(x1);
        if abs(x2 - x1)<eps
            break;
        end
        x1 = x2;
    end
    x = x2;

end

function y = phi(x)
    y = sqrt(20 - x^3)/2;
%    y = sqrt(20/(x+4));
end


