
function openAccordions()
{
    var elements=document.getElementsByClassName("accordion");
    for (var i=0;i<elements.length;i++)
    {
        var element=elements[i];
        openAccordion(element);
    }

}

function openAccordion(element:Element)
{
        element.classList.add('active');
        var panel=element.nextElementSibling as HTMLElement;
        if (panel.style.maxHeight)
        {
        }
        else
        {
            panel.style.maxHeight=panel.scrollHeight+'px';
        }
}